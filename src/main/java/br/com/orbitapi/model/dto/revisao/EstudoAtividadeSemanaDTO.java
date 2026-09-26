package br.com.orbitapi.model.dto.revisao;

import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estudo de uma atividade na semana")
public record EstudoAtividadeSemanaDTO(

        @Schema(description = "Atividade, no estado atual")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Minutos, com cada sessão arredondada para o minuto mais próximo", example = "180")
        long minutos,

        @Schema(description = "Sessões da atividade na semana", example = "3")
        long sessoes

) {}