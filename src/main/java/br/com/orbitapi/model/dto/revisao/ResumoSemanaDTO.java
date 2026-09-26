package br.com.orbitapi.model.dto.revisao;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Números de uma semana")
public record ResumoSemanaDTO(

        @Schema(description = "Tarefas concluídas num dia da semana, pela data de conclusão", example = "8")
        long concluidas,

        @Schema(description = "Tarefas criadas na semana, até agora", example = "5")
        long criadas,

        @Schema(description = "Tarefas com data na semana que estão atrasadas", example = "1")
        long atrasadas,

        @Schema(description = "Tarefas não canceladas com data na semana, até hoje", example = "10")
        long planejadas,

        @Schema(description = "Das planejadas, as concluídas", example = "7")
        long planejadasConcluidas,

        @Schema(description = "planejadasConcluidas / planejadas, de 0 a 1; null sem planejadas", example = "0.7")
        Double taxaConclusao,

        @Schema(description = "Minutos das sessões que começaram na semana", example = "340")
        long minutosEstudo,

        @Schema(description = "Quantidade dessas sessões", example = "6")
        long sessoes,

        @Schema(description = "Dias distintos com sessão ou tarefa concluída", example = "5")
        long diasComAtividade

) {}