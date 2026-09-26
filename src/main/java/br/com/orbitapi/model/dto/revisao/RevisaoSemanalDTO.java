package br.com.orbitapi.model.dto.revisao;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Tudo o que a tela Revisão semanal mostra sobre uma semana")
public record RevisaoSemanalDTO(

        @Schema(description = "Domingo que abre a semana", example = "2026-09-20")
        LocalDate inicioSemana,

        @Schema(description = "Sábado que fecha a semana", example = "2026-09-26")
        LocalDate fimSemana,

        @Schema(description = "Hoje está dentro da semana", example = "true")
        boolean emAndamento,

        @Schema(description = "Dias da semana até hoje, inclusive; 0 numa semana futura e 7 numa passada", example = "6")
        int diasDecorridos,

        @Schema(description = "Números desta semana")
        ResumoSemanaDTO resumo,

        @Schema(description = "Os mesmos números da semana anterior, para comparação")
        ResumoSemanaDTO semanaAnterior,

        @Schema(description = "Os 7 dias, de domingo a sábado")
        List<DiaRevisaoDTO> porDia,

        @Schema(description = "Estudo da semana")
        EstudosSemanaDTO estudos,

        @Schema(description = "Tarefas planejadas para a semana inteira")
        TarefasSemanaDTO tarefas,

        @Schema(description = "O que vem na semana seguinte")
        ProximaSemanaDTO proximaSemana,

        @Schema(description = "Nota livre da semana; null quando não há")
        NotaSemanaDTO nota

) {}