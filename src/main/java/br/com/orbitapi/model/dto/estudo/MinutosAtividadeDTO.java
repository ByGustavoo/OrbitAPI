package br.com.orbitapi.model.dto.estudo;

import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;

public record MinutosAtividadeDTO(ResumoAtividadeDTO atividade, long minutos) {}