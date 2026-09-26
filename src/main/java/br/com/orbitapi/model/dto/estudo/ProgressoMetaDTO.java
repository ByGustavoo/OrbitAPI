package br.com.orbitapi.model.dto.estudo;

import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Progresso de uma atividade na meta semanal")
public record ProgressoMetaDTO(

        @Schema(description = "Atividade, no estado atual")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Meta semanal em minutos", example = "180")
        int metaMinutos,

        @Schema(description = "Minutos estudados na semana, com cada sessão arredondada para o minuto mais próximo", example = "95")
        long minutosRealizados

) {}