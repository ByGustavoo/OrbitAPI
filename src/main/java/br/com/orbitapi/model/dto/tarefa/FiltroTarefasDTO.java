package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record FiltroTarefasDTO(
        LocalDate data,
        LocalDate dataInicial,
        LocalDate dataFinal,
        boolean semData,
        List<Situacao> situacoes,
        List<Prioridade> prioridades,
        Prazo prazo,
        Long categoriaId,
        String busca,
        OrdenacaoTarefas ordenacao,
        int pagina,
        int tamanho
) {
    public FiltroTarefasDTO {
        situacoes = situacoes == null ? List.of() : situacoes.stream().filter(Objects::nonNull).toList();
        prioridades = prioridades == null ? List.of() : prioridades.stream().filter(Objects::nonNull).toList();
        busca = busca == null || busca.isBlank() ? null : busca;
        ordenacao = ordenacao == null ? OrdenacaoTarefas.DATA : ordenacao;
        pagina = Math.max(0, pagina);
        tamanho = Math.clamp(tamanho, 1, 100);
    }
}