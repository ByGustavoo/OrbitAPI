package br.com.orbitapi.model.dto.categoria;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar ou alterar uma categoria")
public record CategoriaEnvioDTO(

        @Schema(description = "Nome; espaços das pontas removidos e repetidos reduzidos a um", example = "Viagens")
        @NotBlank(message = "Informe um nome para a categoria!")
        @Size(max = 40, message = "Use no máximo {max} caracteres no nome. Agora são ${validatedValue.length()}!")
        String nome,

        @Schema(description = "Cor da paleta fixa", example = "CIANO")
        @NotNull(message = "Escolha uma das cores da lista!")
        Cor cor

) {
    public CategoriaEnvioDTO {
        nome = nome == null ? null : nome.replaceAll("[\\s\\p{Z}]+", " ").strip();
    }
}