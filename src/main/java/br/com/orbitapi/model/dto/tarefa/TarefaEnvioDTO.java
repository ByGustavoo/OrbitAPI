package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.validation.TarefaConsistente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@TarefaConsistente
@Schema(description = "Dados para criar ou alterar uma tarefa, já normalizados antes da validação")
public record TarefaEnvioDTO(

        @Schema(description = "Título; espaços das pontas removidos", example = "Academia")
        @NotBlank(message = "Informe um título para a tarefa!")
        @Size(max = 120, message = "Use no máximo {max} caracteres no título. Agora são ${validatedValue.length()}!")
        String titulo,

        @Schema(description = "Descrição; vazia ou só com espaços vira null", example = "Treino de pernas")
        @Size(max = 2000, message = "Use no máximo 2.000 caracteres na descrição. Agora são ${validatedValue.length()}!")
        String descricao,

        @Schema(description = "Data da tarefa; null quando não tem data", example = "2026-09-28")
        LocalDate data,

        @Schema(description = "true quando a tarefa ocupa o dia todo, sem horário", example = "false")
        boolean diaInteiro,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Horário de início, em HH:mm", example = "07:00", type = "string")
        LocalTime horarioInicio,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Horário de fim, em HH:mm; depois do início", example = "08:00", type = "string")
        LocalTime horarioFim,

        @Schema(description = "Prioridade", example = "BAIXA")
        @NotNull(message = "Escolha uma das prioridades da lista!")
        Prioridade prioridade,

        @Schema(description = "Situação escolhida pela pessoa", example = "PENDENTE")
        @NotNull(message = "Escolha uma das situações da lista!")
        Situacao situacao,

        @Schema(description = "Id da categoria; null quando não tem categoria", example = "4")
        Long categoriaId,

        @Schema(description = "Id de uma atividade de estudo não arquivada; null quando não tem atividade", example = "1")
        Long atividadeId,

        @Schema(description = "Minutos de antecedência do lembrete: 0, 5, 15, 30 ou 60; exige data e horário de início", example = "15")
        Integer lembreteMinutosAntes,

        @Schema(description = "Regra de repetição; exige data, que vira a primeira ocorrência")
        RecorrenciaDTO recorrencia

) {
    public TarefaEnvioDTO {
        var semData = data == null;
        var semHorario = semData || diaInteiro || horarioInicio == null;
        var diaInteiroNormalizado = !semData && (diaInteiro || semHorario && horarioFim == null);

        titulo = titulo == null ? null : titulo.strip();
        descricao = descricao == null || descricao.isBlank() ? null : descricao.strip();
        horarioInicio = semData || diaInteiro ? null : horarioInicio;
        horarioFim = semData || diaInteiro ? null : horarioFim;
        lembreteMinutosAntes = semHorario ? null : lembreteMinutosAntes;
        recorrencia = semData ? null : recorrencia;
        diaInteiro = diaInteiroNormalizado;
    }
}