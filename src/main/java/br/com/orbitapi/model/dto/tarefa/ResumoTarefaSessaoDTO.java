package br.com.orbitapi.model.dto.tarefa;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumo da tarefa a partir da qual a sessão começou")
public record ResumoTarefaSessaoDTO(

        @Schema(description = "Identificador", example = "42")
        Long id,

        @Schema(description = "Título atual da tarefa", example = "Revisar a lição 12")
        String titulo

) {}