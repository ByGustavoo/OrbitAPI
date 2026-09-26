package br.com.orbitapi.model.dto.historico;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valor antes e depois de uma mudança registrada")
public record AlteracaoDTO(

        @Schema(description = "Valor anterior: prioridade, data (AAAA-MM-DD) ou situação; null quando não havia", example = "BAIXA")
        String anterior,

        @Schema(description = "Valor novo; null quando deixou de haver", example = "MEDIA")
        String novo

) {}