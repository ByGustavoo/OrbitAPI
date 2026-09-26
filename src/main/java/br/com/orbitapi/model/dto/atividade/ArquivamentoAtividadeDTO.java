package br.com.orbitapi.model.dto.atividade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Novo estado de arquivamento de uma atividade de estudo")
public record ArquivamentoAtividadeDTO(

        @Schema(description = "true arquiva a atividade; false a devolve ao cronômetro e às metas", example = "true")
        @NotNull(message = "Informe se a atividade fica arquivada!")
        Boolean arquivada

) {}