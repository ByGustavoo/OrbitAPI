package br.com.orbitapi.controller.estudo;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.estudo.MapaCalorDTO;
import br.com.orbitapi.model.dto.estudo.ProgressoMetaDTO;
import br.com.orbitapi.model.dto.estudo.ResumoEstudosDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Estudos", description = "Leituras agregadas das sessões de estudo")
public interface EstudoDocs {

    @GetMapping("/resumo")
    @Operation(
            summary = "Resume o tempo de estudo",
            description = """
                    Soma o tempo efetivo e as sessões do período, ou de todo o histórico sem datas, \
                    com a média por sessão arredondada. O período compara o dia do início de cada \
                    sessão no fuso do cabeçalho X-Fuso-Horario.

                    porDia só vem com as duas datas, com um item por dia, inclusive os zerados. \
                    porAtividade traz as atividades com sessão no período, da mais estudada para a \
                    menos, com o maior fim entre as sessões delas.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data em formato inválido ou data inicial depois da final!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ResumoEstudosDTO> buscarResumoEstudos(
            @Parameter(description = "Primeiro dia; sem as datas, todo o histórico", example = "2026-09-20")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia", example = "2026-09-26")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,

            @Parameter(description = "Id da atividade", example = "1")
            @RequestParam(required = false) Long atividadeId);

    @GetMapping("/progresso-semanal")
    @Operation(
            summary = "Mostra o progresso das metas da semana",
            description = """
                    Lista as atividades não arquivadas com meta semanal, em ordem alfabética, com os \
                    minutos estudados nos 7 dias a partir de inicioSemana. Cada sessão é arredondada \
                    para o minuto mais próximo antes de somar e conta no dia do início, no fuso do \
                    cabeçalho X-Fuso-Horario. Atividade com meta e sem estudo aparece com 0.

                    O front envia sempre um domingo; outro dia é aceito e a semana começa nele.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Progresso retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "inicioSemana ausente ou em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<ProgressoMetaDTO>> buscarProgressoSemanal(
            @Parameter(description = "Primeiro dia da semana, normalmente um domingo", example = "2026-09-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioSemana);

    @GetMapping("/mapa-calor")
    @Operation(
            summary = "Gera o mapa de calor de estudo",
            description = """
                    Devolve os minutos estudados em cada dia do intervalo, inclusive os zerados e os \
                    futuros, e a atividade com mais minutos, ou null sem estudo. Cada sessão é \
                    arredondada para o minuto mais próximo antes de somar e conta no dia do início, \
                    no fuso do cabeçalho X-Fuso-Horario. Os níveis de cor ficam com o front.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mapa de calor retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data ausente, em formato inválido ou inicial depois da final!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<MapaCalorDTO> buscarMapaCalor(
            @Parameter(description = "Primeiro dia", example = "2026-04-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia", example = "2026-09-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal);
}