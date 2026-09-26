package br.com.orbitapi.service.revisao;

import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.exceptions.SemanaInvalidaException;
import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.revisao.*;
import br.com.orbitapi.model.dto.tarefa.ConclusoesDiaDTO;
import br.com.orbitapi.model.dto.tarefa.PrazoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.entity.revisao.NotaSemana;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.model.mapper.revisao.NotaSemanaMapper;
import br.com.orbitapi.model.mapper.tarefa.TarefaMapper;
import br.com.orbitapi.repository.revisao.NotaSemanaRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.repository.tarefa.EventoTarefaRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.estudo.EstudoService;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import br.com.orbitapi.service.tarefa.SerieRecorrenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RevisaoSemanalService {

    private final Clock clock;
    private final TarefaMapper tarefaMapper;
    private final EstudoService estudoService;
    private final NotaSemanaMapper notaSemanaMapper;
    private final TarefaRepository tarefaRepository;
    private final SessaoRepository sessaoRepository;
    private final FusoHorarioService fusoHorarioService;
    private final NotaSemanaRepository notaSemanaRepository;
    private final EventoTarefaRepository eventoTarefaRepository;
    private final SerieRecorrenciaService serieRecorrenciaService;
    private static final Set<Prioridade> IMPORTANTES = Set.of(Prioridade.ALTA, Prioridade.URGENTE);
    private static final Collator ORDEM_ALFABETICA = Collator.getInstance(Locale.forLanguageTag("pt-BR"));

    @Transactional
    public RevisaoSemanalDTO buscar(LocalDate inicioSemana) {
        log.info("Buscando a revisão semanal... - Semana: {}", inicioSemana);

        if (inicioSemana.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new SemanaInvalidaException("Informe inicioSemana como um domingo no formato AAAA-MM-DD!");
        }

        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var fuso = fusoHorarioService.obter();
        var hoje = LocalDate.ofInstant(agora, fuso);
        var fimSemana = inicioSemana.plusDays(6);

        serieRecorrenciaService.estender(agora);
        var semana = consultar(inicioSemana, hoje, fuso, agora);
        var resumo = resumir(semana);

        var porAtividade = semana.porAtividade()
                .stream()
                .sorted(Comparator.comparingLong(EstudoAtividadeSemanaDTO::minutos).reversed().thenComparing(estudo -> estudo.atividade().nome(), ORDEM_ALFABETICA))
                .toList();

        return new RevisaoSemanalDTO(
                inicioSemana,
                fimSemana,
                !hoje.isBefore(inicioSemana) && !hoje.isAfter(fimSemana),
                hoje.isBefore(inicioSemana) ? 0 : (int) Math.min(7, ChronoUnit.DAYS.between(inicioSemana, hoje) + 1),
                resumo,
                resumir(consultar(inicioSemana.minusWeeks(1), hoje, fuso, agora)),
                semana.porDia(),
                new EstudosSemanaDTO(resumo.minutosEstudo(), resumo.sessoes(), porAtividade, estudoService.buscarProgressoSemanal(inicioSemana)),
                listarTarefas(inicioSemana, semana.contagem(), fuso, agora),
                planejarProximaSemana(inicioSemana.plusWeeks(1), fuso, agora),
                notaSemanaRepository.findById(inicioSemana).map(notaSemanaMapper::toDTO).orElse(null));
    }

    @Transactional
    public Optional<NotaSemanaDTO> salvarNota(LocalDate inicioSemana, NotaSemanaEnvioDTO notaSemanaEnvioDTO) {
        log.info("Salvando a nota da semana... - Início: {}", inicioSemana);

        if (inicioSemana.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw new SemanaInvalidaException("A semana precisa começar num domingo!");
        }

        if (notaSemanaEnvioDTO.texto().isEmpty()) {
            notaSemanaRepository.deleteById(inicioSemana);
            return Optional.empty();
        }

        var nota = notaSemanaRepository.findById(inicioSemana).orElseGet(NotaSemana::new);

        nota.setInicioSemana(inicioSemana);
        nota.setTexto(notaSemanaEnvioDTO.texto());
        nota.setAtualizadoEm(Instant.now(clock).truncatedTo(ChronoUnit.MILLIS));

        return Optional.of(notaSemanaMapper.toDTO(notaSemanaRepository.save(nota)));
    }

    private DadosSemanaDTO consultar(LocalDate inicioSemana, LocalDate hoje, ZoneId fuso, Instant agora) {
        var inicio = fusoHorarioService.inicioDoDia(inicioSemana);
        var fim = fusoHorarioService.inicioDoDia(inicioSemana.plusWeeks(1));

        var conclusoes = tarefaRepository.contarConclusoesPorDia(inicio, fim, fuso)
                .stream()
                .collect(Collectors.toMap(ConclusoesDiaDTO::data, ConclusoesDiaDTO::quantidade));

        var minutos = sessaoRepository.somarMinutosPorDia(inicio, fim, fuso)
                .stream()
                .collect(Collectors.toMap(MinutosDiaDTO::data, MinutosDiaDTO::minutos));

        var porDia = inicioSemana.datesUntil(inicioSemana.plusWeeks(1))
                .map(data -> new DiaRevisaoDTO(data, conclusoes.getOrDefault(data, 0L), minutos.getOrDefault(data, 0L)))
                .toList();

        return new DadosSemanaDTO(
                tarefaRepository.contarSemana(inicioSemana, hoje, fuso, agora),
                porDia,
                sessaoRepository.somarMinutosESessoesPorAtividade(inicio, fim),
                eventoTarefaRepository.contarNoPeriodo(TipoEventoTarefa.TAREFA_CRIADA, inicio, fim, agora));
    }

    private ResumoSemanaDTO resumir(DadosSemanaDTO semana) {
        var contagem = semana.contagem();

        return new ResumoSemanaDTO(
                semana.porDia().stream().mapToLong(DiaRevisaoDTO::tarefasConcluidas).sum(),
                semana.criadas(),
                contagem.atrasadas(),
                contagem.planejadasAteHoje(),
                contagem.concluidasAteHoje(),
                contagem.planejadasAteHoje() == 0 ? null : (double) contagem.concluidasAteHoje() / contagem.planejadasAteHoje(),
                semana.porDia().stream().mapToLong(DiaRevisaoDTO::minutosEstudo).sum(),
                semana.porAtividade().stream().mapToLong(EstudoAtividadeSemanaDTO::sessoes).sum(),
                semana.porDia().stream().filter(dia -> dia.tarefasConcluidas() > 0 || dia.minutosEstudo() > 0).count());
    }

    private TarefasSemanaDTO listarTarefas(LocalDate inicioSemana, ContagemTarefasSemanaDTO contagem, ZoneId fuso, Instant agora) {
        var pendentes = carregar(tarefaRepository.buscarEmAberto(inicioSemana, inicioSemana.plusDays(6), true, fuso, agora));

        return new TarefasSemanaDTO(
                contagem.planejadas(),
                contagem.concluidas(),
                contagem.concluidasComAtraso(),
                contagem.emAberto(),
                contagem.atrasadas(),
                contagem.naoRealizadas(),
                contagem.canceladas(),
                pendentes,
                contarImportantes(pendentes));
    }

    private ProximaSemanaDTO planejarProximaSemana(LocalDate inicioSemana, ZoneId fuso, Instant agora) {
        var agendadas = carregar(tarefaRepository.buscarEmAberto(inicioSemana, inicioSemana.plusDays(6), false, fuso, agora));

        return new ProximaSemanaDTO(
                inicioSemana,
                inicioSemana.plusDays(6),
                agendadas.size(),
                contarImportantes(agendadas),
                tarefaRepository.contarPorPrazo(Prazo.ATRASADA, fuso, agora),
                agendadas.subList(0, Math.min(10, agendadas.size())));
    }

    private List<TarefaDTO> carregar(List<PrazoTarefaDTO> prazos) {
        var tarefas = tarefaRepository.findComResumosByIdIn(prazos.stream().map(PrazoTarefaDTO::id).toList())
                .stream()
                .collect(Collectors.toMap(Tarefa::getId, Function.identity()));

        return prazos.stream()
                .map(prazo -> tarefaMapper.toDTO(tarefas.get(prazo.id()), prazo.prazo()))
                .toList();
    }

    private long contarImportantes(List<TarefaDTO> tarefas) {
        return tarefas.stream().filter(tarefa -> IMPORTANTES.contains(tarefa.prioridade())).count();
    }
}