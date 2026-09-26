package br.com.orbitapi.controller.categoria;

import br.com.orbitapi.exceptions.dto.ErrorResponseDTO;
import br.com.orbitapi.model.dto.categoria.CategoriaDTO;
import br.com.orbitapi.model.dto.categoria.CategoriaEnvioDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Categoria", description = "Endpoints relacionados às categorias de tarefas")
public interface CategoriaDocs {

    @GetMapping
    @Operation(
            summary = "Lista as categorias",
            description = """
                    Retorna todas as categorias em ordem alfabética do nome (pt-BR), cada uma com a \
                    quantidade de tarefas ligadas a ela. Alimenta a tela de Configurações, o \
                    formulário de tarefa e o filtro da página Tarefas.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorias retornadas com sucesso!"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<List<CategoriaDTO>> listarCategorias();

    @PostMapping
    @Operation(
            summary = "Cria uma categoria",
            description = """
                    Cria uma categoria pela tela de Configurações. O nome chega sem os espaços das \
                    pontas e com os repetidos reduzidos a um, e só então é validado.

                    O nome é único sem diferenciar maiúsculas: "saúde" conflita com "Saúde". A nova \
                    categoria volta com quantidadeTarefas igual a 0.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria criada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nome vazio ou longo demais, cor ausente ou fora da paleta!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe uma categoria com esse nome!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CategoriaDTO> salvarCategoria(@RequestBody @Valid CategoriaEnvioDTO categoriaEnvioDTO);

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualiza uma categoria",
            description = """
                    Troca o nome e a cor de uma categoria, com as mesmas regras da criação. A \
                    checagem de nome repetido ignora a própria categoria, então mudar só as \
                    maiúsculas do nome é permitido.

                    O nome e a cor novos aparecem em todas as tarefas da categoria, e a mudança não \
                    gera evento no Histórico.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria atualizada com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Nome vazio ou longo demais, cor ausente ou fora da paleta!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe outra categoria com esse nome!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<CategoriaDTO> atualizarCategoria(
            @Parameter(description = "Id da categoria", example = "2")
            @PathVariable Long id,

            @RequestBody @Valid CategoriaEnvioDTO categoriaEnvioDTO);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Exclui uma categoria",
            description = """
                    Remove a categoria de forma definitiva e responde sem corpo. A exclusão é \
                    sempre permitida: as tarefas da categoria continuam existindo, sem categoria. \
                    Não há arquivamento nem desfazer.""")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Categoria excluída com sucesso!"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Id em formato inválido!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor!",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    ResponseEntity<Void> deletarCategoria(
            @Parameter(description = "Id da categoria", example = "2")
            @PathVariable Long id);
}