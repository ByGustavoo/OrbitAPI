package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.Prioridade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Carga de tarefas não canceladas de um dia")
public record DiaCalendarioDTO(

        @Schema(description = "Dia", example = "2026-09-24")
        LocalDate data,

        @Schema(description = "Tarefas do dia, sem as canceladas", example = "6")
        long quantidade,

        @Schema(description = "Maior prioridade entre as tarefas contadas, inclusive as concluídas", example = "URGENTE")
        Prioridade maiorPrioridade,

        @Schema(description = "Tarefas do dia com prazo ATRASADA", example = "1")
        long atrasadas,

        @Schema(description = "Tarefas do dia com situação CONCLUIDA", example = "2")
        long concluidas

) {}