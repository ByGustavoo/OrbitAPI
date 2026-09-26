package br.com.orbitapi.model.dto.estudo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Minutos estudados num dia, contados pelo dia do início de cada sessão")
public record MinutosDiaDTO(

        @Schema(description = "Dia", example = "2026-09-02")
        LocalDate data,

        @Schema(description = "Soma dos minutos, com cada sessão arredondada para o minuto mais próximo", example = "45")
        long minutos

) {}