package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.model.dto.revisao.ContagemTarefasSemanaDTO;
import br.com.orbitapi.model.dto.dashboard.ContagemTarefasDashboardDTO;
import br.com.orbitapi.model.dto.tarefa.ConclusoesDiaDTO;
import br.com.orbitapi.model.dto.tarefa.DiaCalendarioDTO;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.model.dto.tarefa.PrazoTarefaDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public interface TarefaConsultaRepository {

    List<PrazoTarefaDTO> buscarPagina(FiltroTarefasDTO filtro, ZoneId fuso, Instant agora);

    long contar(FiltroTarefasDTO filtro, ZoneId fuso, Instant agora);

    List<DiaCalendarioDTO> resumirPorDia(LocalDate dataInicial, LocalDate dataFinal, ZoneId fuso, Instant agora);

    ContagemTarefasSemanaDTO contarSemana(LocalDate inicioSemana, LocalDate hoje, ZoneId fuso, Instant agora);

    List<PrazoTarefaDTO> buscarEmAberto(LocalDate dataInicial, LocalDate dataFinal, boolean semNaoRealizadas, ZoneId fuso, Instant agora);

    long contarPorPrazo(Prazo prazo, ZoneId fuso, Instant agora);

    List<ConclusoesDiaDTO> contarConclusoesPorDia(Instant inicio, Instant fim, ZoneId fuso);

    ContagemTarefasDashboardDTO contarParaDashboard(LocalDate inicioSemana, ZoneId fuso, Instant agora);
}