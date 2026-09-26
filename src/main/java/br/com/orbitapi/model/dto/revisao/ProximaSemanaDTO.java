package br.com.orbitapi.model.dto.revisao;

import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "A semana seguinte à revisada")
public record ProximaSemanaDTO(

        @Schema(description = "Domingo da semana seguinte", example = "2026-09-27")
        LocalDate inicioSemana,

        @Schema(description = "Sábado da semana seguinte", example = "2026-10-03")
        LocalDate fimSemana,

        @Schema(description = "Tarefas pendentes ou em andamento com data nessa semana", example = "9")
        long agendadas,

        @Schema(description = "Das agendadas, as de prioridade ALTA ou URGENTE", example = "3")
        long altaPrioridade,

        @Schema(description = "Todas as tarefas atrasadas hoje, de qualquer data", example = "2")
        long atrasadasEmAberto,

        @Schema(description = "As 10 primeiras agendadas, por data")
        List<TarefaDTO> tarefas

) {}