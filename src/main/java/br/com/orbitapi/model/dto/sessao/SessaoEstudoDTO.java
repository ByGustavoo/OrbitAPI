package br.com.orbitapi.model.dto.sessao;

import br.com.orbitapi.enums.ModoCronometro;
import br.com.orbitapi.enums.OrigemSessao;
import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import br.com.orbitapi.model.dto.tarefa.ResumoTarefaSessaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Período de estudo salvo pelo cronômetro ou lançado à mão")
public record SessaoEstudoDTO(

        @Schema(description = "Identificador", example = "7")
        Long id,

        @Schema(description = "Atividade estudada, no estado atual")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Tarefa a partir da qual o estudo começou; null quando não há")
        ResumoTarefaSessaoDTO tarefa,

        @Schema(description = "Modo do cronômetro", example = "POMODORO")
        ModoCronometro modo,

        @Schema(description = "Origem da sessão", example = "CRONOMETRO")
        OrigemSessao origem,

        @Schema(description = "Instante do início; define o dia da sessão", example = "2026-09-24T21:00:00Z")
        Instant inicio,

        @Schema(description = "Instante do fim", example = "2026-09-24T21:58:00Z")
        Instant fim,

        @Schema(description = "Tempo efetivo de estudo, sem pausas", example = "3000")
        int duracaoSegundos,

        @Schema(description = "Ciclos de foco concluídos; só em POMODORO", example = "2")
        Integer ciclosConcluidos,

        @Schema(description = "Observação livre; null quando não há", example = "Revisei a lição 12.")
        String observacao

) {}