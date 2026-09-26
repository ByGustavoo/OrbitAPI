package br.com.orbitapi.model.dto.historico;

import br.com.orbitapi.enums.AreaHistorico;
import br.com.orbitapi.enums.TipoEventoHistorico;
import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import br.com.orbitapi.model.dto.categoria.ResumoCategoriaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Registro da linha do tempo: evento de tarefa, sessão de estudo ou tarefa não realizada")
public record RegistroHistoricoDTO(

        @Schema(description = "evento-{id}, sessao-{id} ou nao-realizada-{idTarefa}", example = "evento-812")
        String id,

        @Schema(description = "Tipo do registro", example = "PRIORIDADE_ALTERADA")
        TipoEventoHistorico tipo,

        @Schema(description = "Área do registro", example = "TAREFAS")
        AreaHistorico area,

        @Schema(description = "Quando aconteceu", example = "2026-09-24T16:35:00Z")
        Instant ocorridoEm,

        @Schema(description = "false só na tarefa não realizada sem horário", example = "true")
        boolean comHorario,

        @Schema(description = "Título da tarefa no momento do evento, ou nome da atividade na sessão", example = "Levar o carro para a revisão")
        String titulo,

        @Schema(description = "Tarefa ligada ao registro; null quando não há", example = "42")
        Long tarefaId,

        @Schema(description = "Sessão do registro; só em SESSAO_ESTUDO", example = "77")
        Long sessaoId,

        @Schema(description = "Categoria atual da tarefa; null quando não há")
        ResumoCategoriaDTO categoria,

        @Schema(description = "Atividade da sessão; null nos registros de tarefa")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Antes e depois em PRIORIDADE_ALTERADA, DATA_ALTERADA e TAREFA_REABERTA; null nos demais")
        AlteracaoDTO alteracao,

        @Schema(description = "Duração efetiva da sessão; só em SESSAO_ESTUDO", example = "2700")
        Integer duracaoSegundos

) {}