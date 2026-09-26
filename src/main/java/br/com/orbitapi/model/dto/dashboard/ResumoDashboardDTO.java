package br.com.orbitapi.model.dto.dashboard;

import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.tarefa.ConclusoesDiaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Indicadores, gráficos e atividade recente do Dashboard")
public record ResumoDashboardDTO(

        @Schema(description = "Primeiro dia do período dos gráficos", example = "2026-09-18")
        LocalDate dataInicial,

        @Schema(description = "Último dia do período dos gráficos", example = "2026-09-24")
        LocalDate dataFinal,

        @Schema(description = "Indicadores da semana atual e das tarefas em aberto")
        ContagensDashboardDTO contagens,

        @Schema(description = "Um item por dia do período, pela data de conclusão")
        List<ConclusoesDiaDTO> concluidasPorDia,

        @Schema(description = "Um item por dia do período, pelo dia do início da sessão")
        List<MinutosDiaDTO> minutosEstudoPorDia,

        @Schema(description = "As quatro prioridades, de BAIXA a URGENTE, com as tarefas em aberto")
        List<QuantidadePrioridadeDTO> distribuicaoPrioridade,

        @Schema(description = "Os 6 registros mais recentes da linha do tempo")
        List<EventoRecenteDTO> eventosRecentes

) {}