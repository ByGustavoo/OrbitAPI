package br.com.orbitapi.model.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Indicadores do Dashboard")
public record ContagensDashboardDTO(

        @Schema(description = "Tarefas concluídas num dia da semana atual, pela data de conclusão", example = "6")
        long concluidas,

        @Schema(description = "Pendentes no prazo com data na semana atual", example = "4")
        long pendentes,

        @Schema(description = "Em andamento no prazo com data na semana atual", example = "1")
        long emAndamento,

        @Schema(description = "Todas as atrasadas, de qualquer data", example = "4")
        long atrasadas,

        @Schema(description = "Urgentes pendentes ou em andamento, com qualquer data ou sem data", example = "1")
        long urgentes

) {}