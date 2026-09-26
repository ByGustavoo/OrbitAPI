package br.com.orbitapi.controller.dashboard;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.dashboard.ResumoDashboardDTO;
import br.com.orbitapi.model.dto.dashboard.SequenciaDTO;
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

@Tag(name = "Dashboard", description = "Leituras agregadas da tela inicial")
public interface DashboardDocs {

    @GetMapping("/resumo")
    @Operation(
            summary = "Resume o Dashboard",
            description = """
                    Devolve os indicadores da semana atual (domingo a sábado), as tarefas em aberto \
                    por prioridade, um item por dia do período nos gráficos de conclusões e de \
                    minutos de estudo, e os 6 registros mais recentes da linha do tempo: criação, \
                    conclusão, cancelamento e reabertura de tarefas, e sessões salvas.

                    Os dias e os prazos seguem o fuso do cabeçalho X-Fuso-Horario, e cada sessão é \
                    arredondada para o minuto mais próximo antes de somar.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resumo retornado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data ausente, em formato inválido ou inicial depois da final!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<ResumoDashboardDTO> buscarResumoDashboard(
            @Parameter(description = "Primeiro dia dos gráficos", example = "2026-09-18")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia dos gráficos, normalmente hoje", example = "2026-09-24")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal);

    @GetMapping("/sequencia")
    @Operation(
            summary = "Calcula a sequência de dias com atividade",
            description = """
                    Um dia tem atividade quando tem uma sessão, pelo dia do início, ou uma tarefa \
                    concluída, pelo dia da conclusão, no fuso do cabeçalho X-Fuso-Horario.

                    A sequência atual termina na data, se ela já tem atividade, ou na véspera. O \
                    recorde considera só os dias até a data.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sequência retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<SequenciaDTO> buscarSequencia(
            @Parameter(description = "Dia de referência; sem ele, hoje", example = "2026-09-24")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data);
}