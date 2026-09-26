package br.com.orbitapi.model.dto.dashboard;

import br.com.orbitapi.enums.TipoEventoRecente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Registro recente da linha do tempo")
public record EventoRecenteDTO(

        @Schema(description = "Tipo do registro", example = "TAREFA_CONCLUIDA")
        TipoEventoRecente tipo,

        @Schema(description = "Título da tarefa, ou nome da atividade na sessão", example = "Pagar a conta de luz")
        String descricao,

        @Schema(description = "Instante do registro; na sessão, o início", example = "2026-09-24T16:50:00Z")
        Instant ocorridoEm,

        @Schema(description = "Id da sessão em SESSAO_SALVA; id da tarefa nos demais", example = "51")
        Long referenciaId

) {}