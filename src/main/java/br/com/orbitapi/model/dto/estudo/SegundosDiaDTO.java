package br.com.orbitapi.model.dto.estudo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Tempo estudado num dia, contado pelo dia do início de cada sessão")
public record SegundosDiaDTO(

        @Schema(description = "Dia", example = "2026-09-21")
        LocalDate data,

        @Schema(description = "Soma das durações efetivas", example = "2700")
        long segundos,

        @Schema(description = "Sessões do dia", example = "1")
        long sessoes

) {}