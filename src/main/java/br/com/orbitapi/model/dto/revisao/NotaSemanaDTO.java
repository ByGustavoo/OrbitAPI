package br.com.orbitapi.model.dto.revisao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Nota livre de uma semana")
public record NotaSemanaDTO(

        @Schema(description = "Texto da nota", example = "Semana puxada no trabalho, mas mantive o inglês.")
        String texto,

        @Schema(description = "Instante da última gravação", example = "2026-09-24T23:10:00Z")
        Instant atualizadoEm

) {}