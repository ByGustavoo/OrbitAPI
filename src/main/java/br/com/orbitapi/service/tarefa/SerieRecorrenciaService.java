package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.enums.DiaSemana;
import br.com.orbitapi.model.dto.tarefa.RecorrenciaDTO;
import br.com.orbitapi.model.entity.tarefa.SerieRecorrencia;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.model.mapper.tarefa.TarefaMapper;
import br.com.orbitapi.repository.tarefa.SerieRecorrenciaRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class SerieRecorrenciaService {

    private final TarefaMapper tarefaMapper;
    private final TarefaRepository tarefaRepository;
    private final FusoHorarioService fusoHorarioService;
    private final SerieRecorrenciaRepository serieRecorrenciaRepository;

    @Transactional
    public void iniciar(Tarefa primeira, RecorrenciaDTO recorrenciaDTO, Instant agora, Set<LocalDate> datasOcupadas) {
        log.info("Iniciando a série recorrente... - Tarefa: [{}] - Frequência: {}", primeira.getId(), recorrenciaDTO.frequencia());
        var hoje = LocalDate.ofInstant(agora, fusoHorarioService.obter());
        var serie = tarefaMapper.toSerie(recorrenciaDTO);

        serie.setDataInicial(primeira.getData());
        serie.setGeradaAte(primeira.getData());
        primeira.setSerie(serieRecorrenciaRepository.save(serie));

        gerar(serie, primeira, primeira.getData().plusDays(1), fimDaJanela(serie, hoje), agora, datasOcupadas);
    }

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true, condition = "#result")
    public boolean estender(Instant agora) {
        var hoje = LocalDate.ofInstant(agora, fusoHorarioService.obter());
        var series = serieRecorrenciaRepository.buscarParaEstender(hoje.plusMonths(3));

        series.forEach(serie -> estender(serie, hoje, agora));

        return !series.isEmpty();
    }

    @Transactional
    public void encerrarAntesDe(SerieRecorrencia serie, LocalDate data) {
        log.info("Encerrando a série recorrente... - ID: [{}] - Antes de: {}", serie.getId(), data);

        if (tarefaRepository.existsBySerieIdAndDataBefore(serie.getId(), data)) {
            serie.setDataFim(data.minusDays(1));
            serie.setGeradaAte(data.minusDays(1));
            return;
        }

        tarefaRepository.findBySerieId(serie.getId()).forEach(tarefa -> tarefa.setSerie(null));
        serieRecorrenciaRepository.delete(serie);
    }

    private void estender(SerieRecorrencia serie, LocalDate hoje, Instant agora) {
        log.info("Estendendo a série recorrente... - ID: [{}] - Gerada até: {}", serie.getId(), serie.getGeradaAte());

        tarefaRepository.findFirstBySerieIdOrderByDataDesc(serie.getId())
                .ifPresentOrElse(
                        modelo -> gerar(serie, modelo, serie.getGeradaAte().plusDays(1), fimDaJanela(serie, hoje), agora, Set.of()),
                        () -> serieRecorrenciaRepository.delete(serie));
    }

    private void gerar(SerieRecorrencia serie, Tarefa modelo, LocalDate de, LocalDate ate, Instant agora, Set<LocalDate> datasOcupadas) {
        var ocorrencias = gerarDatas(serie, de, ate)
                .stream()
                .filter(data -> !datasOcupadas.contains(data))
                .map(data -> tarefaMapper.toOcorrencia(modelo, data, agora))
                .toList();

        if (ate.isAfter(serie.getGeradaAte())) {
            serie.setGeradaAte(ate);
        }

        tarefaRepository.saveAll(ocorrencias);
    }

    private List<LocalDate> gerarDatas(SerieRecorrencia serie, LocalDate de, LocalDate ate) {
        var inicio = serie.getDataInicial();
        var limite = limitar(ate, serie.getDataFim());

        if (limite.isBefore(de)) {
            return List.of();
        }

        var datas = switch (serie.getFrequencia()) {
            case MENSAL -> somarMeses(inicio, 1, limite);
            case ANUAL -> somarMeses(inicio, 12, limite);
            case DIARIA -> inicio.datesUntil(limite.plusDays(1));
            case SEMANAL -> inicio.datesUntil(limite.plusDays(1), Period.ofWeeks(1));
            case DIAS_DA_SEMANA -> inicio.datesUntil(limite.plusDays(1))
                    .filter(data -> data.equals(inicio) || serie.getDiasSemana().contains(diaSemana(data)));
        };

        return datas.filter(data -> !data.isBefore(de)).toList();
    }

    private Stream<LocalDate> somarMeses(LocalDate inicio, int meses, LocalDate limite) {
        return Stream.iterate(0L, indice -> indice + 1)
                .map(indice -> inicio.plusMonths(indice * meses))
                .takeWhile(data -> !data.isAfter(limite));
    }

    private DiaSemana diaSemana(LocalDate data) {
        return DiaSemana.values()[data.getDayOfWeek().getValue() % 7];
    }

    private LocalDate fimDaJanela(SerieRecorrencia serie, LocalDate hoje) {
        return limitar(hoje.plusMonths(12), serie.getDataFim());
    }

    private LocalDate limitar(LocalDate data, LocalDate dataFim) {
        return dataFim != null && dataFim.isBefore(data) ? dataFim : data;
    }
}