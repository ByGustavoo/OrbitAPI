package br.com.orbitapi.controller.historico;

import br.com.orbitapi.enums.AreaHistorico;
import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.historico.DetalheHistoricoDTO;
import br.com.orbitapi.model.dto.historico.RegistroHistoricoDTO;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Histórico", description = "Linha do tempo de tarefas e estudos")
public interface HistoricoDocs {

    @GetMapping
    @Operation(
            summary = "Lista a linha do tempo",
            description = """
                    Junta, numa página só, os eventos das tarefas que já aconteceram, as sessões de \
                    estudo e as tarefas não realizadas, calculadas pelo prazo. O período compara o \
                    dia de ocorridoEm no fuso do cabeçalho X-Fuso-Horario, e a não realizada ocorre \
                    na data e no horário de início dela, ou às 23:59 quando não tem horário.

                    Ordena por ocorridoEm decrescente e, no empate, pelo id decrescente. A busca \
                    procura no título, no nome da categoria e no nome da atividade, sem diferenciar \
                    maiúsculas.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Linha do tempo retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data ausente ou inválida, data inicial depois da final, ou área fora da lista!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PaginaDTO<RegistroHistoricoDTO>> listarHistorico(
            @Parameter(description = "Primeiro dia", example = "2026-09-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia", example = "2026-09-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,

            @Parameter(description = "Só tarefas ou só estudos; sem ele, as duas", example = "TAREFAS")
            @RequestParam(required = false) AreaHistorico area,

            @Parameter(description = "Texto no título, na categoria ou na atividade", example = "relatório")
            @RequestParam(required = false) String busca,

            @Parameter(description = "Página, a partir de 0", example = "0")
            @RequestParam(defaultValue = "0") int pagina,

            @Parameter(description = "Itens por página, de 1 a 100", example = "30")
            @RequestParam(defaultValue = "30") int tamanho);

    @GetMapping("/{id}")
    @Operation(
            summary = "Busca os detalhes de um registro da linha do tempo",
            description = """
                    Devolve o registro, a tarefa como está agora, com o prazo calculado, e a sessão, \
                    quando houver. Um registro de tarefa excluída, de sessão excluída ou de tarefa \
                    que deixou de ser não realizada responde 404, assim como um id fora do formato.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro não encontrado!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<DetalheHistoricoDTO> buscarRegistroHistorico(
            @Parameter(description = "evento-{n}, sessao-{n} ou nao-realizada-{n}", example = "evento-812")
            @PathVariable String id);
}