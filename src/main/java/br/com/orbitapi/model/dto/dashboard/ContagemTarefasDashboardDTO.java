package br.com.orbitapi.model.dto.dashboard;

import br.com.orbitapi.enums.Prioridade;

import java.util.Map;

public record ContagemTarefasDashboardDTO(
        long pendentes,
        long emAndamento,
        long atrasadas,
        Map<Prioridade, Long> emAbertoPorPrioridade
) {}