package br.com.orbitapi.model.dto.revisao;

import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Tarefas com data na semana inteira")
public record TarefasSemanaDTO(

        @Schema(description = "Não canceladas", example = "12")
        long planejadas,

        @Schema(description = "Concluídas", example = "7")
        long concluidas,

        @Schema(description = "Concluídas depois do prazo", example = "1")
        long concluidasComAtraso,

        @Schema(description = "Pendentes ou em andamento ainda no prazo", example = "3")
        long emAberto,

        @Schema(description = "Atrasadas", example = "1")
        long atrasadas,

        @Schema(description = "Não realizadas", example = "1")
        long naoRealizadas,

        @Schema(description = "Canceladas", example = "0")
        long canceladas,

        @Schema(description = "Pendentes ou em andamento, exceto as não realizadas, por data")
        List<TarefaDTO> pendentes,

        @Schema(description = "Das pendentes, as de prioridade ALTA ou URGENTE", example = "2")
        long importantesPendentes

) {}