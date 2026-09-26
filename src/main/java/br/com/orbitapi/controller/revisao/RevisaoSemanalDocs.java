package br.com.orbitapi.controller.revisao;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.revisao.NotaSemanaDTO;
import br.com.orbitapi.model.dto.revisao.NotaSemanaEnvioDTO;
import br.com.orbitapi.model.dto.revisao.RevisaoSemanalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Revisão Semanal", description = "Endpoints relacionados à revisão semanal")
public interface RevisaoSemanalDocs {

    @GetMapping
    @Operation(
            summary = "Monta a revisão de uma semana",
            description = """
                    Reúne numa chamada só o resumo da semana e o da anterior, os 7 dias, o estudo por \
                    atividade e as metas, as tarefas com data na semana, a semana seguinte e a nota. \
                    Os dias de cada instante (conclusão, criação e início das sessões) e os prazos \
                    seguem o fuso do cabeçalho X-Fuso-Horario.

                    No resumo, planejadas conta só os dias até hoje; em tarefas, a semana inteira. \
                    Cada sessão é arredondada para o minuto mais próximo antes de somar. Destaques e \
                    textos de comparação ficam com o front.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Revisão retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "inicioSemana ausente, fora do formato ou que não é um domingo!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<RevisaoSemanalDTO> buscarRevisaoSemanal(
            @Parameter(description = "Domingo que abre a semana", example = "2026-09-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioSemana);

    @PutMapping("/{inicioSemana}/nota")
    @Operation(
            summary = "Salva, altera ou apaga a nota da semana",
            description = """
                    Grava o texto livre da semana que começa no domingo informado, sem espaços nas \
                    pontas. Cada semana tem uma nota só, e qualquer semana pode ter a sua.

                    Texto vazio, ou só com espaços, apaga a nota e responde 204 sem corpo, mesmo \
                    quando a semana não tinha nota.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Nota salva com sucesso!"),
            @ApiResponse(
                    responseCode = "204",
                    description = "Nota apagada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Semana fora do formato ou que não começa num domingo, texto ausente ou com mais de 1000 caracteres!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<NotaSemanaDTO> salvarNotaSemana(
            @Parameter(description = "Domingo que abre a semana", example = "2026-09-20")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioSemana,

            @RequestBody @Valid NotaSemanaEnvioDTO notaSemanaEnvioDTO);
}