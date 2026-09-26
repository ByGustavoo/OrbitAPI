package br.com.orbitapi.model.dto.revisao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Texto da nota livre de uma semana")
public record NotaSemanaEnvioDTO(

        @Schema(description = "Texto; espaços das pontas removidos, e vazio apaga a nota", example = "Semana puxada no trabalho, mas mantive o inglês.")
        @NotNull(message = "Envie o texto da nota!")
        @Size(max = 1000, message = "A nota pode ter até {max} caracteres!")
        String texto

) {
    public NotaSemanaEnvioDTO {
        texto = texto == null ? null : texto.strip();
    }
}