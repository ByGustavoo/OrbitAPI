package br.com.orbitapi.model.dto.categoria;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo da categoria dentro de uma tarefa, sempre no estado atual")
public record ResumoCategoriaDTO(

        @Schema(description = "Identificador", example = "3")
        Long id,

        @Schema(description = "Nome atual da categoria", example = "Casa e família")
        String nome,

        @Schema(description = "Cor atual da categoria", example = "VERDE")
        Cor cor

) {}