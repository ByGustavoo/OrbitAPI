package br.com.orbitapi.exceptions.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Erro de validação de um campo da requisição")
public record MethodArgumentNotValidResponseDTO(

        @Schema(description = "Nome do campo no DTO de entrada", example = "nome")
        String campo,

        @Schema(description = "Mensagem para a pessoa", example = "Informe um nome para a categoria!")
        String mensagem

) {}