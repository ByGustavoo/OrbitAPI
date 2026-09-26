package br.com.orbitapi.model.dto.tarefa;

import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.entity.categoria.Categoria;

import java.time.Instant;

public record AlteracaoTarefaDTO(TarefaEnvioDTO tarefaEnvioDTO, Categoria categoria, AtividadeEstudo atividade, Instant agora) {}