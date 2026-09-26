package br.com.orbitapi.service.dashboard;

import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.exceptions.PeriodoInvalidoException;
import br.com.orbitapi.model.dto.dashboard.ContagensDashboardDTO;
import br.com.orbitapi.model.dto.dashboard.QuantidadePrioridadeDTO;
import br.com.orbitapi.model.dto.dashboard.ResumoDashboardDTO;
import br.com.orbitapi.model.dto.dashboard.SequenciaDTO;
import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.tarefa.ConclusoesDiaDTO;
import br.com.orbitapi.repository.dashboard.DashboardRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import br.com.orbitapi.service.tarefa.SerieRecorrenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final Clock clock;
    private final TarefaRepository tarefaRepository;
    private final SessaoRepository sessaoRepository;
    private final FusoHorarioService fusoHorarioService;
    private final DashboardRepository dashboardRepository;
    private final SerieRecorrenciaService serieRecorrenciaService;

    @Transactional
    public ResumoDashboardDTO resumir(LocalDate dataInicial, LocalDate dataFinal) {
        log.info("Resumindo o Dashboard... - De: {} - Até: {}", dataInicial, dataFinal);

        if (dataInicial.isAfter(dataFinal)) {
            throw new PeriodoInvalidoException("Informe um período válido!");
        }

        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var fuso = fusoHorarioService.obter();
        var inicioSemana = LocalDate.ofInstant(agora, fuso).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        var inicio = fusoHorarioService.inicioDoDia(dataInicial);
        var fim = fusoHorarioService.inicioDoDia(dataFinal.plusDays(1));

        serieRecorrenciaService.estender(agora);
        var tarefas = tarefaRepository.contarParaDashboard(inicioSemana, fuso, agora);

        var concluidasNaSemana = tarefaRepository.contarConclusoesPorDia(fusoHorarioService.inicioDoDia(inicioSemana), fusoHorarioService.inicioDoDia(inicioSemana.plusWeeks(1)), fuso)
                .stream()
                .mapToLong(ConclusoesDiaDTO::quantidade)
                .sum();

        var conclusoes = tarefaRepository.contarConclusoesPorDia(inicio, fim, fuso)
                .stream()
                .collect(Collectors.toMap(ConclusoesDiaDTO::data, ConclusoesDiaDTO::quantidade));

        var minutos = sessaoRepository.somarMinutosPorDia(inicio, fim, fuso)
                .stream()
                .collect(Collectors.toMap(MinutosDiaDTO::data, MinutosDiaDTO::minutos));

        return new ResumoDashboardDTO(
                dataInicial,
                dataFinal,
                new ContagensDashboardDTO(
                        concluidasNaSemana,
                        tarefas.pendentes(),
                        tarefas.emAndamento(),
                        tarefas.atrasadas(),
                        tarefas.emAbertoPorPrioridade().get(Prioridade.URGENTE)),
                dataInicial.datesUntil(dataFinal.plusDays(1)).map(data -> new ConclusoesDiaDTO(data, conclusoes.getOrDefault(data, 0L))).toList(),
                dataInicial.datesUntil(dataFinal.plusDays(1)).map(data -> new MinutosDiaDTO(data, minutos.getOrDefault(data, 0L))).toList(),
                Arrays.stream(Prioridade.values()).map(prioridade -> new QuantidadePrioridadeDTO(prioridade, tarefas.emAbertoPorPrioridade().get(prioridade))).toList(),
                dashboardRepository.buscarEventosRecentes(agora, 6));
    }

    @Cacheable("dashboard")
    @Transactional(readOnly = true)
    public SequenciaDTO buscarSequencia(LocalDate data) {
        log.info("Buscando a sequência de dias... - Data: {}", data);
        var fuso = fusoHorarioService.obter();
        var referencia = data == null ? LocalDate.ofInstant(Instant.now(clock), fuso) : data;
        var dias = new HashSet<>(dashboardRepository.listarDiasComAtividade(referencia, fuso));
        var contaHoje = dias.contains(referencia);

        var atual = 0;
        var cursor = contaHoje ? referencia : referencia.minusDays(1);

        while (dias.contains(cursor)) {
            atual++;
            cursor = cursor.minusDays(1);
        }

        var recorde = 0;

        for (var dia : dias) {
            if (!dias.contains(dia.minusDays(1))) {
                var tamanho = 1;

                while (dias.contains(dia.plusDays(tamanho))) {
                    tamanho++;
                }

                recorde = Math.max(recorde, tamanho);
            }
        }

        return new SequenciaDTO(atual, recorde, contaHoje);
    }
}