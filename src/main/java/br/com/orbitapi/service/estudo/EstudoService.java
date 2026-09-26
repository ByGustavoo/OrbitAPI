package br.com.orbitapi.service.estudo;

import br.com.orbitapi.exceptions.PeriodoInvalidoException;
import br.com.orbitapi.model.dto.estudo.EstudoPorAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MapaCalorDTO;
import br.com.orbitapi.model.dto.estudo.MinutosAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.estudo.ProgressoMetaDTO;
import br.com.orbitapi.model.dto.estudo.ResumoEstudosDTO;
import br.com.orbitapi.model.dto.estudo.SegundosDiaDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.mapper.estudo.EstudoMapper;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EstudoService {

    private final EstudoMapper estudoMapper;
    private final SessaoRepository sessaoRepository;
    private final FusoHorarioService fusoHorarioService;
    private final AtividadeRepository atividadeRepository;
    private static final Collator ORDEM_ALFABETICA = Collator.getInstance(Locale.forLanguageTag("pt-BR"));

    @Cacheable("estudos")
    @Transactional(readOnly = true)
    public ResumoEstudosDTO resumir(LocalDate dataInicial, LocalDate dataFinal, Long atividadeId) {
        log.info("Resumindo os estudos... - De: {} - Até: {} - Atividade: [{}]", dataInicial, dataFinal, atividadeId);

        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new PeriodoInvalidoException("A data inicial precisa ser antes da final!");
        }

        var idAtividade = atividadeId == null || atividadeId <= 0 ? null : atividadeId;
        var inicio = fusoHorarioService.inicioDoDia(dataInicial);
        var fim = fusoHorarioService.inicioDoDia(dataFinal == null ? null : dataFinal.plusDays(1));
        var porAtividade = sessaoRepository.somarPorAtividade(inicio, fim, idAtividade);

        var totalSegundos = porAtividade.stream().mapToLong(EstudoPorAtividadeDTO::segundos).sum();
        var totalSessoes = porAtividade.stream().mapToLong(EstudoPorAtividadeDTO::sessoes).sum();
        var media = totalSessoes == 0 ? 0 : Math.round((double) totalSegundos / totalSessoes);

        var porDia = dataInicial == null || dataFinal == null
                ? List.<SegundosDiaDTO>of()
                : preencherDias(dataInicial, dataFinal, sessaoRepository.somarPorDia(inicio, fim, idAtividade, fusoHorarioService.obter()));

        return new ResumoEstudosDTO(totalSegundos, totalSessoes, media, porDia, porAtividade);
    }

    @Cacheable("estudos")
    @Transactional(readOnly = true)
    public List<ProgressoMetaDTO> buscarProgressoSemanal(LocalDate inicioSemana) {
        log.info("Buscando o progresso das metas... - Semana: {}", inicioSemana);
        var inicio = fusoHorarioService.inicioDoDia(inicioSemana);
        var fim = fusoHorarioService.inicioDoDia(inicioSemana.plusDays(7));

        var realizados = sessaoRepository.somarMinutosPorAtividade(inicio, fim)
                .stream()
                .collect(Collectors.toMap(minutos -> minutos.atividade().id(), MinutosAtividadeDTO::minutos));

        return atividadeRepository.findByArquivadaFalseAndMetaSemanalMinutosNotNull()
                .stream()
                .sorted(Comparator.comparing(AtividadeEstudo::getNome, ORDEM_ALFABETICA))
                .map(atividade -> estudoMapper.toProgressoMeta(atividade, realizados.getOrDefault(atividade.getId(), 0L)))
                .toList();
    }

    @Cacheable("estudos")
    @Transactional(readOnly = true)
    public MapaCalorDTO gerarMapaCalor(LocalDate dataInicial, LocalDate dataFinal) {
        log.info("Gerando o mapa de calor... - De: {} - Até: {}", dataInicial, dataFinal);

        if (dataInicial.isAfter(dataFinal)) {
            throw new PeriodoInvalidoException("A data inicial precisa ser antes da final!");
        }

        var inicio = fusoHorarioService.inicioDoDia(dataInicial);
        var fim = fusoHorarioService.inicioDoDia(dataFinal.plusDays(1));

        var minutosPorData = sessaoRepository.somarMinutosPorDia(inicio, fim, fusoHorarioService.obter())
                .stream()
                .collect(Collectors.toMap(MinutosDiaDTO::data, MinutosDiaDTO::minutos));

        var dias = dataInicial.datesUntil(dataFinal.plusDays(1))
                .map(data -> new MinutosDiaDTO(data, minutosPorData.getOrDefault(data, 0L)))
                .toList();

        var maisEstudada = sessaoRepository.somarMinutosPorAtividade(inicio, fim)
                .stream()
                .findFirst()
                .map(estudoMapper::toAtividadeMaisEstudada)
                .orElse(null);

        return new MapaCalorDTO(dias, maisEstudada);
    }

    private List<SegundosDiaDTO> preencherDias(LocalDate dataInicial, LocalDate dataFinal, List<SegundosDiaDTO> diasComSessao) {
        var porData = diasComSessao.stream().collect(Collectors.toMap(SegundosDiaDTO::data, Function.identity()));

        return dataInicial.datesUntil(dataFinal.plusDays(1))
                .map(data -> porData.getOrDefault(data, new SegundosDiaDTO(data, 0, 0)))
                .toList();
    }
}