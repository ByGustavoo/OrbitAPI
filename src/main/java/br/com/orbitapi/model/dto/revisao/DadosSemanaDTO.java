package br.com.orbitapi.model.dto.revisao;

import java.util.List;

public record DadosSemanaDTO(
        ContagemTarefasSemanaDTO contagem,
        List<DiaRevisaoDTO> porDia,
        List<EstudoAtividadeSemanaDTO> porAtividade,
        long criadas
) {}