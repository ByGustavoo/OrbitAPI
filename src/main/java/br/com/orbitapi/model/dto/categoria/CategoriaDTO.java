package br.com.orbitapi.model.dto.categoria;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categoria que agrupa tarefas, com a contagem de tarefas ligadas a ela")
public record CategoriaDTO(

        @Schema(description = "Identificador", example = "3")
        Long id,

        @Schema(description = "Nome, único sem diferenciar maiúsculas", example = "Casa e família")
        String nome,

        @Schema(description = "Cor da paleta fixa", example = "VERDE")
        Cor cor,

        @Schema(description = "Tarefas ligadas à categoria, em qualquer situação", example = "25")
        long quantidadeTarefas

) {}