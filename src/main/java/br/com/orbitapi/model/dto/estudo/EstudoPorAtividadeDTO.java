package br.com.orbitapi.model.dto.estudo;

import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Tempo estudado de uma atividade no período")
public record EstudoPorAtividadeDTO(

        @Schema(description = "Atividade, no estado atual")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Soma das durações efetivas", example = "7200")
        long segundos,

        @Schema(description = "Sessões da atividade no período", example = "3")
        long sessoes,

        @Schema(description = "Maior fim entre as sessões da atividade no período", example = "2026-09-24T21:58:00Z")
        Instant ultimaSessaoEm

) {}