package br.com.orbitapi.repository.sessao;

import br.com.orbitapi.model.dto.estudo.EstudoPorAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MinutosAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.estudo.SegundosDiaDTO;
import br.com.orbitapi.model.dto.revisao.EstudoAtividadeSemanaDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public interface SessaoConsultaRepository {

    List<SegundosDiaDTO> somarPorDia(Instant inicio, Instant fim, Long atividadeId, ZoneId fuso);

    List<EstudoPorAtividadeDTO> somarPorAtividade(Instant inicio, Instant fim, Long atividadeId);

    List<MinutosAtividadeDTO> somarMinutosPorAtividade(Instant inicio, Instant fim);

    List<MinutosDiaDTO> somarMinutosPorDia(Instant inicio, Instant fim, ZoneId fuso);

    List<EstudoAtividadeSemanaDTO> somarMinutosESessoesPorAtividade(Instant inicio, Instant fim);
}