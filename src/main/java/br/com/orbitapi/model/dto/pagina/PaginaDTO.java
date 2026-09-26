package br.com.orbitapi.model.dto.pagina;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Página de uma lista, no formato próprio do contrato")
public record PaginaDTO<T>(

        @Schema(description = "Itens da página pedida")
        List<T> itens,

        @Schema(description = "Página devolvida, a partir de 0", example = "0")
        int pagina,

        @Schema(description = "Tamanho efetivamente usado, de 1 a 100", example = "20")
        int tamanho,

        @Schema(description = "Total de itens que atendem aos filtros", example = "57")
        long totalItens,

        @Schema(description = "Total de páginas; 0 quando não há itens", example = "3")
        int totalPaginas

) {}