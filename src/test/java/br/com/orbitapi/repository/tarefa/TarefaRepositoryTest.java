package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@SpringBootTest
class TarefaRepositoryTest extends AbstractTest {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void countByCategoriaIdTest() {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Estudos")).getId();

        var quantidade = Assertions.assertDoesNotThrow(() -> tarefaRepository.countByCategoriaId(idCategoria));
        Assertions.assertEquals(1, quantidade);
    }

    @Test
    void findComResumosByIdTest() {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Revisar verbos irregulares")).getId();

        var tarefa = Assertions.assertDoesNotThrow(() -> tarefaRepository.findComResumosById(idTarefa));
        Assertions.assertTrue(tarefa.isPresent());
    }

    @Test
    void findComResumosByIdInTest() {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Revisar verbos irregulares")).getId();

        var tarefas = Assertions.assertDoesNotThrow(() -> tarefaRepository.findComResumosByIdIn(List.of(idTarefa)));
        Assertions.assertEquals(1, tarefas.size());
    }

    @Test
    void buscarPaginaTest() {
        var filtro = new FiltroTarefasDTO(null, null, null, true, null, null, null, null, "lâmpada", OrdenacaoTarefas.ATUALIZACAO, 0, 20);

        var prazos = Assertions.assertDoesNotThrow(() -> tarefaRepository.buscarPagina(filtro, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(1, prazos.size());
    }

    @Test
    void contarTest() {
        var filtro = new FiltroTarefasDTO(null, null, null, false, List.of(Situacao.PENDENTE), null, null, null, null, OrdenacaoTarefas.DATA, 0, 20);

        var quantidade = Assertions.assertDoesNotThrow(() -> tarefaRepository.contar(filtro, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(37, quantidade);
    }

    @Test
    void resumirPorDiaTest() {
        var dias = Assertions.assertDoesNotThrow(() -> tarefaRepository.resumirPorDia(LocalDate.now().minusDays(7), LocalDate.now().plusDays(7), ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertFalse(dias.isEmpty());
    }

    @Test
    void findFirstBySerieIdOrderByDataDescTest() {
        var idSerie = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Meditação")).getSerie().getId();

        var tarefa = Assertions.assertDoesNotThrow(() -> tarefaRepository.findFirstBySerieIdOrderByDataDesc(idSerie));
        Assertions.assertTrue(tarefa.isPresent());
    }

    @Test
    void findBySerieIdAndSituacaoInAndDataBetweenTest() {
        var idSerie = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Meditação")).getSerie().getId();

        var tarefas = Assertions.assertDoesNotThrow(() -> tarefaRepository.findBySerieIdAndSituacaoInAndDataBetween(idSerie, List.of(Situacao.PENDENTE), LocalDate.now().minusDays(7), LocalDate.now()));
        Assertions.assertFalse(tarefas.isEmpty());
    }

    @Test
    void contarPorCategoriaTest() {
        var quantidades = Assertions.assertDoesNotThrow(() -> tarefaRepository.contarPorCategoria());
        Assertions.assertEquals(2, quantidades.size());
    }

    @Test
    void findBySerieIdTest() {
        var idSerie = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Meditação")).getSerie().getId();

        var tarefas = Assertions.assertDoesNotThrow(() -> tarefaRepository.findBySerieId(idSerie));
        Assertions.assertEquals(34, tarefas.size());
    }

    @Test
    void existsBySerieIdAndDataBeforeTest() {
        var serie = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Meditação")).getSerie();

        var existe = Assertions.assertDoesNotThrow(() -> tarefaRepository.existsBySerieIdAndDataBefore(serie.getId(), serie.getDataInicial()));
        Assertions.assertFalse(existe);
    }

    @Test
    void findBySerieIdAndDataAfterAndSituacaoInTest() {
        var serie = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Meditação")).getSerie();

        var tarefas = Assertions.assertDoesNotThrow(() -> tarefaRepository.findBySerieIdAndDataAfterAndSituacaoIn(serie.getId(), serie.getDataInicial(), List.of(Situacao.PENDENTE)));
        Assertions.assertEquals(33, tarefas.size());
    }

    @Test
    void contarSemanaTest() {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1);

        var contagem = Assertions.assertDoesNotThrow(() -> tarefaRepository.contarSemana(inicioSemana, LocalDate.now(), ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(7, contagem.planejadas());
        Assertions.assertEquals(7, contagem.emAberto());
        Assertions.assertEquals(0, contagem.planejadasAteHoje());
    }

    @Test
    void buscarEmAbertoTest() {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1);

        var prazos = Assertions.assertDoesNotThrow(() -> tarefaRepository.buscarEmAberto(inicioSemana, inicioSemana.plusDays(6), true, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(7, prazos.size());
    }

    @Test
    void contarPorPrazoTest() {
        var quantidade = Assertions.assertDoesNotThrow(() -> tarefaRepository.contarPorPrazo(Prazo.ATRASADA, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(1, quantidade);
    }

    @Test
    void contarConclusoesPorDiaTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Trocar a lâmpada"));

        tarefa.setSituacao(Situacao.CONCLUIDA);
        tarefa.setDataConclusao(Instant.now());
        tarefaRepository.saveAndFlush(tarefa);

        var dias = Assertions.assertDoesNotThrow(() -> tarefaRepository.contarConclusoesPorDia(Instant.now().minus(1, ChronoUnit.DAYS), Instant.now().plus(1, ChronoUnit.DAYS), ZoneId.of("America/Sao_Paulo")));
        Assertions.assertEquals(1, dias.size());
    }

    @Test
    void contarParaDashboardTest() {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        var contagem = Assertions.assertDoesNotThrow(() -> tarefaRepository.contarParaDashboard(inicioSemana, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(1, contagem.atrasadas());
        Assertions.assertEquals(1, contagem.emAbertoPorPrioridade().get(Prioridade.BAIXA));
        Assertions.assertEquals(0, contagem.emAbertoPorPrioridade().get(Prioridade.URGENTE));
    }
}