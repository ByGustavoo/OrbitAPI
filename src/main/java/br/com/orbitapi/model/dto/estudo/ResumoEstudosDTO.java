package br.com.orbitapi.model.dto.estudo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Métricas de estudo de um período, ou de todo o histórico")
public record ResumoEstudosDTO(

        @Schema(description = "Soma das durações efetivas", example = "12600")
        long totalSegundos,

        @Schema(description = "Sessões no período", example = "5")
        long totalSessoes,

        @Schema(description = "Média arredondada por sessão; 0 sem sessões", example = "2520")
        long mediaSegundosPorSessao,

        @Schema(description = "Um item por dia do período, inclusive zerados; vazia sem as duas datas")
        List<SegundosDiaDTO> porDia,

        @Schema(description = "Atividades com sessão no período, da mais estudada para a menos")
        List<EstudoPorAtividadeDTO> porAtividade

) {}