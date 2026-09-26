package br.com.orbitapi.model.dto.sessao;

import java.time.LocalDate;

public record FiltroSessoesDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        Long atividadeId,
        int pagina,
        int tamanho
) {
    public FiltroSessoesDTO {
        atividadeId = atividadeId == null || atividadeId <= 0 ? null : atividadeId;
        pagina = Math.max(0, pagina);
        tamanho = Math.clamp(tamanho, 1, 100);
    }
}