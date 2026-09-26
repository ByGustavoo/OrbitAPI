package br.com.orbitapi.model.dto.revisao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Um dia da semana revisada")
public record DiaRevisaoDTO(

        @Schema(description = "Dia", example = "2026-09-22")
        LocalDate data,

        @Schema(description = "Tarefas concluídas no dia, pela data de conclusão", example = "2")
        long tarefasConcluidas,

        @Schema(description = "Minutos das sessões que começaram no dia", example = "50")
        long minutosEstudo

) {}