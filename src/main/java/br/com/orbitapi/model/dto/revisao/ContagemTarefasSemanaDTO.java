package br.com.orbitapi.model.dto.revisao;

public record ContagemTarefasSemanaDTO(
        long atrasadas,
        long planejadasAteHoje,
        long concluidasAteHoje,
        long planejadas,
        long concluidas,
        long concluidasComAtraso,
        long emAberto,
        long naoRealizadas,
        long canceladas
) {}