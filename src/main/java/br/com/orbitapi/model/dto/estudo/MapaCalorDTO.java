package br.com.orbitapi.model.dto.estudo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Minutos de estudo por dia de um intervalo, para o mapa de calor")
public record MapaCalorDTO(

        @Schema(description = "Um item por dia do intervalo, inclusive zerados e futuros")
        List<MinutosDiaDTO> dias,

        @Schema(description = "Atividade com mais minutos no intervalo; null quando não houve estudo")
        AtividadeMaisEstudadaDTO atividadeMaisEstudada

) {}