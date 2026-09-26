package br.com.orbitapi.controller.sessao;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Sessão de Estudo", description = "Endpoints relacionados às sessões de estudo")
public interface SessaoDocs {

    @GetMapping
    @Operation(
            summary = "Lista as sessões de estudo",
            description = """
                    Devolve uma página de sessões, das mais recentes para as mais antigas. O período \
                    compara o dia do início da sessão, no fuso do cabeçalho X-Fuso-Horario: uma \
                    sessão que passa da meia-noite conta no dia em que começou.

                    Página negativa vira 0, tamanho fora de 1 a 100 é ajustado para o limite mais \
                    próximo e um atividadeId que não é positivo é ignorado.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de sessões retornada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Data em formato inválido ou data inicial depois da final!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<PaginaDTO<SessaoEstudoDTO>> listarSessoes(
            @Parameter(description = "Primeiro dia do início das sessões", example = "2026-09-19")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,

            @Parameter(description = "Último dia do início das sessões", example = "2026-09-25")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,

            @Parameter(description = "Id da atividade", example = "1")
            @RequestParam(required = false) Long atividadeId,

            @Parameter(description = "Página, a partir de 0", example = "0")
            @RequestParam(defaultValue = "0") int pagina,

            @Parameter(description = "Itens por página, de 1 a 100", example = "20")
            @RequestParam(defaultValue = "20") int tamanho);

    @PostMapping
    @Operation(
            summary = "Salva uma sessão de estudo",
            description = """
                    Salva uma sessão do cronômetro ou lançada à mão. Antes de validar, modo diferente \
                    de POMODORO vira LIVRE, origem diferente de CRONOMETRO vira MANUAL, os ciclos só \
                    ficam em POMODORO e a observação perde os espaços das pontas.

                    A atividade precisa existir e estar ativa. A duração vai de 1 minuto a 24 horas e \
                    não passa do tempo entre início e fim, com 1 segundo de tolerância; início e fim \
                    podem estar no máximo 1 minuto depois de agora. Uma tarefa inexistente é \
                    descartada sem erro, e salvar a sessão não muda a situação da tarefa.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sessão salva com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Atividade inexistente ou arquivada, horários ou duração inválidos, ou observação longa demais!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<SessaoEstudoDTO> salvarSessao(@RequestBody @Valid SessaoEnvioDTO sessaoEnvioDTO);

    @PutMapping("/{id}")
    @Operation(
            summary = "Corrige uma sessão de estudo",
            description = """
                    Troca a atividade, o início, o fim, a duração, os ciclos e a observação, com as \
                    mesmas validações da criação. A atividade arquivada é aceita quando é a mesma que \
                    a sessão já tinha.

                    Modo, origem e tarefa ligada não mudam: os valores gravados ficam e os do corpo \
                    são ignorados. Os ciclos só ficam numa sessão gravada como POMODORO.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão corrigida com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Atividade inexistente ou arquivada, horários ou duração inválidos, ou observação longa demais!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sessão não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<SessaoEstudoDTO> atualizarSessao(
            @Parameter(description = "Id da sessão", example = "7")
            @PathVariable Long id,

            @RequestBody @Valid SessaoEnvioDTO sessaoEnvioDTO);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Exclui uma sessão de estudo",
            description = """
                    Remove a sessão de forma definitiva e responde sem corpo. Ela some do histórico, \
                    das metas, dos gráficos, do mapa de calor e da sequência de dias.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sessão excluída com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sessão não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> deletarSessao(
            @Parameter(description = "Id da sessão", example = "7")
            @PathVariable Long id);
}