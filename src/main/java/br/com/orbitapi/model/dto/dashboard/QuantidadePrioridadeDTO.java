package br.com.orbitapi.model.dto.dashboard;

import br.com.orbitapi.enums.Prioridade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tarefas em aberto de uma prioridade")
public record QuantidadePrioridadeDTO(

        @Schema(description = "Prioridade", example = "ALTA")
        Prioridade prioridade,

        @Schema(description = "Pendentes e em andamento de qualquer data", example = "3")
        long quantidade

) {}