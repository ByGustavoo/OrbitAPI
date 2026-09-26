package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.DiaSemana;
import br.com.orbitapi.enums.Frequencia;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Schema(description = "Regra de repetição de uma tarefa recorrente")
public record RecorrenciaDTO(

        @Schema(description = "Frequência da repetição", example = "DIAS_DA_SEMANA")
        Frequencia frequencia,

        @Schema(description = "Dias da semana, de domingo a sábado; só em DIAS_DA_SEMANA e null nas demais frequências", example = "[\"SEGUNDA\", \"QUARTA\", \"SEXTA\"]")
        List<DiaSemana> diasSemana,

        @Schema(description = "Último dia possível da série; null quando nunca termina", example = "2026-12-18")
        LocalDate dataFim

) {
    public RecorrenciaDTO {
        diasSemana = frequencia != Frequencia.DIAS_DA_SEMANA ? null : diasSemana == null ? List.of() : diasSemana.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }
}