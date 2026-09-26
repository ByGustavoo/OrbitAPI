package br.com.orbitapi.model.dto.tarefa;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Tarefas concluídas num dia, pela data de conclusão")
public record ConclusoesDiaDTO(

        @Schema(description = "Dia", example = "2026-09-18")
        LocalDate data,

        @Schema(description = "Quantidade de tarefas concluídas no dia", example = "2")
        long quantidade

) {}