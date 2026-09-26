package br.com.orbitapi.model.dto.atividade;

import br.com.orbitapi.enums.Cor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Atividade de estudo usada no cronômetro, nas metas e nas tarefas")
public record AtividadeEstudoDTO(

        @Schema(description = "Identificador", example = "1")
        Long id,

        @Schema(description = "Nome, único entre as não arquivadas sem diferenciar maiúsculas", example = "Inglês")
        String nome,

        @Schema(description = "Cor da paleta fixa", example = "AZUL")
        Cor cor,

        @Schema(description = "Meta semanal em minutos, de 1 a 6000; null quando não há meta", example = "180")
        Integer metaSemanalMinutos,

        @Schema(description = "Arquivada: some do cronômetro e das metas, mas mantém o histórico", example = "false")
        boolean arquivada

) {}