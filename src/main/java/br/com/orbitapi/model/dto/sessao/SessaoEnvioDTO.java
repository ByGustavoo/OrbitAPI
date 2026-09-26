package br.com.orbitapi.model.dto.sessao;

import br.com.orbitapi.enums.ModoCronometro;
import br.com.orbitapi.enums.OrigemSessao;
import br.com.orbitapi.validation.SessaoConsistente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@SessaoConsistente
@Schema(description = "Dados para salvar ou corrigir uma sessão de estudo, já normalizados antes da validação")
public record SessaoEnvioDTO(

        @Schema(description = "Id de uma atividade não arquivada", example = "1")
        @NotNull(message = "Escolha a atividade que você estudou!")
        @Positive(message = "Escolha a atividade que você estudou!")
        Long atividadeId,

        @Schema(description = "Id da tarefa de origem; um id inexistente vira null", example = "42")
        Long tarefaId,

        @Schema(description = "Modo do cronômetro; qualquer valor diferente de POMODORO vira LIVRE", example = "POMODORO")
        ModoCronometro modo,

        @Schema(description = "Origem; qualquer valor diferente de CRONOMETRO vira MANUAL", example = "CRONOMETRO")
        OrigemSessao origem,

        @Schema(description = "Instante do início, em ISO 8601 com fuso", example = "2026-09-24T21:00:00.000Z")
        @NotNull(message = "Informe quando a sessão começou!")
        Instant inicio,

        @Schema(description = "Instante do fim, em ISO 8601 com fuso", example = "2026-09-24T21:58:00.000Z")
        Instant fim,

        @Schema(description = "Tempo efetivo de estudo em segundos, de 60 a 86400", example = "3000")
        @NotNull(message = "A sessão precisa ter pelo menos 1 minuto!")
        @Min(value = 60, message = "A sessão precisa ter pelo menos 1 minuto!")
        @Max(value = 86400, message = "Uma sessão pode ter no máximo 24 horas!")
        Integer duracaoSegundos,

        @Schema(description = "Ciclos de foco concluídos; só mantido em POMODORO", example = "2")
        @PositiveOrZero(message = "Informe um número de ciclos válido!")
        Integer ciclosConcluidos,

        @Schema(description = "Observação; vazia ou só com espaços vira null", example = "Revisei a lição 12.")
        @Size(max = 500, message = "Use no máximo {max} caracteres na observação. Agora são ${validatedValue.length()}!")
        String observacao

) {
    public SessaoEnvioDTO {
        modo = modo == ModoCronometro.POMODORO ? ModoCronometro.POMODORO : ModoCronometro.LIVRE;
        origem = origem == OrigemSessao.CRONOMETRO ? OrigemSessao.CRONOMETRO : OrigemSessao.MANUAL;
        ciclosConcluidos = modo == ModoCronometro.POMODORO ? ciclosConcluidos : null;
        observacao = observacao == null || observacao.isBlank() ? null : observacao.strip();
    }
}