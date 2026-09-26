package br.com.orbitapi.model.dto.estudo;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Atividade com mais minutos de estudo no intervalo")
public record AtividadeMaisEstudadaDTO(

        @Schema(description = "Identificador", example = "1")
        Long id,

        @Schema(description = "Nome atual", example = "Inglês")
        String nome,

        @Schema(description = "Cor atual", example = "AZUL")
        Cor cor,

        @Schema(description = "Minutos estudados no intervalo", example = "1320")
        long minutos

) {}