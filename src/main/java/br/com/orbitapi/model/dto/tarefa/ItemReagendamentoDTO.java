package br.com.orbitapi.model.dto.tarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Uma tarefa e a data nova dela")
public record ItemReagendamentoDTO(

        @Schema(description = "Id da tarefa", example = "17")
        @NotNull(message = "Informe a tarefa a reagendar!")
        Long id,

        @Schema(description = "Data nova", example = "2026-09-25")
        @NotNull(message = "Informe a nova data da tarefa!")
        LocalDate data

) {}