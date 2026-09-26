package br.com.orbitapi.model.dto.historico;

import br.com.orbitapi.enums.AreaHistorico;

import java.time.LocalDate;

public record FiltroHistoricoDTO(
        LocalDate dataInicial,
        LocalDate dataFinal,
        AreaHistorico area,
        String busca,
        int pagina,
        int tamanho
) {
    public FiltroHistoricoDTO {
        busca = busca == null || busca.isBlank() ? null : busca.strip();
        pagina = Math.max(0, pagina);
        tamanho = Math.clamp(tamanho, 1, 100);
    }
}