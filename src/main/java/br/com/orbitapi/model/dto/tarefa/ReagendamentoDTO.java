package br.com.orbitapi.model.dto.tarefa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Novas datas de várias tarefas, aplicadas de uma vez")
public record ReagendamentoDTO(

        @Schema(description = "Tarefas e as datas novas; pelo menos uma")
        @NotEmpty(message = "Informe ao menos uma tarefa para reagendar!")
        List<@NotNull(message = "Informe ao menos uma tarefa para reagendar!") @Valid ItemReagendamentoDTO> itens

) {}