package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import br.com.orbitapi.model.dto.categoria.ResumoCategoriaDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Tarefa simples ou ocorrência de uma série recorrente, com o prazo calculado na leitura")
public record TarefaDTO(

        @Schema(description = "Identificador", example = "42")
        Long id,

        @Schema(description = "Título", example = "Academia")
        String titulo,

        @Schema(description = "Descrição; null quando não tem", example = "Treino de pernas")
        String descricao,

        @Schema(description = "Data da tarefa; null quando não tem data", example = "2026-09-28")
        LocalDate data,

        @Schema(description = "true quando a tarefa ocupa o dia todo, sem horário", example = "false")
        boolean diaInteiro,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Horário de início, em HH:mm", example = "07:00", type = "string")
        LocalTime horarioInicio,

        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "Horário de fim, em HH:mm", example = "08:00", type = "string")
        LocalTime horarioFim,

        @Schema(description = "Prioridade", example = "BAIXA")
        Prioridade prioridade,

        @Schema(description = "Situação escolhida pela pessoa", example = "PENDENTE")
        Situacao situacao,

        @Schema(description = "Prazo calculado no fuso do cabeçalho X-Fuso-Horario", example = "NO_PRAZO")
        Prazo prazo,

        @Schema(description = "Categoria; null quando não tem")
        ResumoCategoriaDTO categoria,

        @Schema(description = "Atividade de estudo ligada; null quando não tem")
        ResumoAtividadeDTO atividade,

        @Schema(description = "Minutos de antecedência do lembrete; null quando não tem", example = "15")
        Integer lembreteMinutosAntes,

        @Schema(description = "Série da qual a tarefa é uma ocorrência; null quando não se repete", example = "7")
        Long serieId,

        @Schema(description = "Regra da série; null quando não se repete")
        RecorrenciaDTO recorrencia,

        @Schema(description = "Instante da conclusão; só com situação CONCLUIDA", example = "2026-09-28T11:05:00Z")
        Instant dataConclusao,

        @Schema(description = "Instante da criação", example = "2026-09-20T13:12:00Z")
        Instant criadoEm,

        @Schema(description = "Instante da última alteração", example = "2026-09-22T18:40:00Z")
        Instant atualizadoEm

) {}