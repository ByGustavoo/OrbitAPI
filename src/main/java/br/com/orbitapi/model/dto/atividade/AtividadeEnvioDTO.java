package br.com.orbitapi.model.dto.atividade;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar ou alterar uma atividade de estudo")
public record AtividadeEnvioDTO(

        @Schema(description = "Nome; espaços das pontas removidos e repetidos reduzidos a um", example = "Redação")
        @NotBlank(message = "Informe um nome para a atividade!")
        @Size(max = 40, message = "Use no máximo {max} caracteres no nome. Agora são ${validatedValue.length()}!")
        String nome,

        @Schema(description = "Cor da paleta fixa", example = "ROSA")
        @NotNull(message = "Escolha uma das cores da lista!")
        Cor cor,

        @Schema(description = "Meta semanal em minutos, de 1 a 6000; null quando não há meta", example = "120")
        @Positive(message = "Informe uma meta maior que zero ou deixe o campo vazio!")
        @Max(value = 6000, message = "A meta pode ter no máximo 100 horas por semana!")
        Integer metaSemanalMinutos

) {
    public AtividadeEnvioDTO {
        nome = nome == null ? null : nome.replaceAll("[\\s\\p{Z}]+", " ").strip();
    }
}