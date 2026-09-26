package br.com.orbitapi.model.dto.revisao;

import br.com.orbitapi.model.dto.estudo.ProgressoMetaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Estudo de uma semana")
public record EstudosSemanaDTO(

        @Schema(description = "Minutos de estudo na semana", example = "340")
        long minutos,

        @Schema(description = "Sessões na semana", example = "6")
        long sessoes,

        @Schema(description = "Atividades estudadas, por minutos decrescentes e depois por nome")
        List<EstudoAtividadeSemanaDTO> porAtividade,

        @Schema(description = "Progresso das metas semanais")
        List<ProgressoMetaDTO> metas

) {}