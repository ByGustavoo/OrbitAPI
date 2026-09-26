package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.Situacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Nova situação de uma tarefa")
public record SituacaoTarefaDTO(

        @Schema(description = "Situação pedida; qualquer transição é permitida", example = "CONCLUIDA")
        @NotNull(message = "Informe uma situação válida!")
        Situacao situacao

) {}