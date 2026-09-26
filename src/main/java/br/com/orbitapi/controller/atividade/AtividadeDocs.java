package br.com.orbitapi.controller.atividade;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.atividade.ArquivamentoAtividadeDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEnvioDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEstudoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Atividade de Estudo", description = "Endpoints relacionados às atividades de estudo")
public interface AtividadeDocs {

    @GetMapping
    @Operation(
            summary = "Lista as atividades de estudo",
            description = """
                    Retorna todas as atividades, inclusive as arquivadas, em ordem alfabética do \
                    nome (pt-BR). Alimenta o cronômetro, as metas e os formulários de tarefa e de \
                    sessão, que filtram as arquivadas do lado do front.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividades retornadas com sucesso!"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<AtividadeEstudoDTO>> listarAtividades();

    @PostMapping
    @Operation(
            summary = "Cria uma atividade de estudo",
            description = """
                    Cria uma atividade não arquivada. O nome chega sem os espaços das pontas e com \
                    os repetidos reduzidos a um, e só então é validado.

                    O nome é único entre as atividades não arquivadas, sem diferenciar maiúsculas: \
                    uma atividade arquivada não impede criar outra com o mesmo nome. A meta semanal \
                    é opcional, de 1 a 6000 minutos.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade criada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nome vazio ou longo demais, cor inválida ou meta fora de 1 a 6000!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe uma atividade ativa com esse nome!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AtividadeEstudoDTO> salvarAtividade(@RequestBody @Valid AtividadeEnvioDTO atividadeEnvioDTO);

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualiza uma atividade de estudo",
            description = """
                    Troca o nome, a cor e a meta semanal, com as mesmas regras da criação. Não muda \
                    o arquivamento, que tem rota própria.

                    O nome é comparado com as outras atividades não arquivadas, inclusive quando a \
                    atividade editada está arquivada. O nome e a cor novos aparecem em todas as \
                    tarefas e sessões ligadas a ela.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade atualizada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nome vazio ou longo demais, cor inválida ou meta fora de 1 a 6000!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe outra atividade ativa com esse nome!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AtividadeEstudoDTO> atualizarAtividade(
            @Parameter(description = "Id da atividade", example = "1")
            @PathVariable Long id,

            @RequestBody @Valid AtividadeEnvioDTO atividadeEnvioDTO);

    @PatchMapping("/{id}/arquivamento")
    @Operation(
            summary = "Arquiva ou desarquiva uma atividade de estudo",
            description = """
                    Com arquivada true, a atividade sai do cronômetro e das metas e deixa de aceitar \
                    sessões e tarefas novas; as sessões antigas e o histórico continuam. Com false, \
                    ela volta a ser ativa.

                    Desarquivar exige que nenhuma outra atividade ativa tenha o mesmo nome, sem \
                    diferenciar maiúsculas. Repetir o estado atual responde 200 sem mudar nada.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Arquivamento alterado com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Campo arquivada ausente ou não booleano!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ao desarquivar, já existe outra atividade ativa com esse nome!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<AtividadeEstudoDTO> alterarArquivamentoAtividade(
            @Parameter(description = "Id da atividade", example = "1")
            @PathVariable Long id,

            @RequestBody @Valid ArquivamentoAtividadeDTO arquivamentoAtividadeDTO);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Exclui uma atividade de estudo",
            description = """
                    Remove de forma definitiva uma atividade que nunca teve sessão e responde sem \
                    corpo. As tarefas ligadas a ela continuam existindo, sem atividade.

                    Uma atividade com sessões registradas não pode ser excluída: arquive-a para \
                    manter o histórico.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Atividade excluída com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Atividade não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "A atividade tem sessões registradas!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> deletarAtividade(
            @Parameter(description = "Id da atividade", example = "1")
            @PathVariable Long id);
}