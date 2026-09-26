package br.com.orbitapi.model.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Sequência de dias seguidos com atividade")
public record SequenciaDTO(

        @Schema(description = "Dias seguidos com atividade até a data, ou até a véspera quando a data ainda não tem", example = "7")
        int atual,

        @Schema(description = "Maior sequência registrada até a data", example = "14")
        int recorde,

        @Schema(description = "A data já tem atividade", example = "true")
        boolean contaHoje

) {}