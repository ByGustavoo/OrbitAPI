package br.com.orbitapi.model.dto.atividade;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo da atividade de estudo dentro de uma tarefa, sempre no estado atual")
public record ResumoAtividadeDTO(

        @Schema(description = "Identificador", example = "1")
        Long id,

        @Schema(description = "Nome atual da atividade", example = "Inglês")
        String nome,

        @Schema(description = "Cor atual da atividade", example = "AZUL")
        Cor cor

) {}