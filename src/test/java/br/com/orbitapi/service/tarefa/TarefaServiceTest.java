package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.DiaSemana;
import br.com.orbitapi.enums.EscopoAlteracao;
import br.com.orbitapi.enums.Frequencia;
import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.model.dto.tarefa.ItemReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.ReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.RecorrenciaDTO;
import br.com.orbitapi.model.dto.tarefa.SituacaoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
class TarefaServiceTest extends AbstractTest {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void listarTest() {
        var filtro = new FiltroTarefasDTO(
                null,
                null,
                null,
                false,
                null,
                null,
                Prazo.ATRASADA,
                null,
                null,
                OrdenacaoTarefas.DATA,
                0,
                20);

        var pagina = Assertions.assertDoesNotThrow(() -> tarefaService.listar(filtro));
        Assertions.assertEquals(1, pagina.totalItens());
    }

    @Test
    void resumirCalendarioTest() {
        var dias = Assertions.assertDoesNotThrow(() -> tarefaService.resumirCalendario(LocalDate.now().minusDays(7), LocalDate.now().plusDays(35)));
        Assertions.assertFalse(dias.isEmpty());
    }

    @Test
    void buscarTest() {
        var idTarefa = tarefaRepository.findAll()
                .stream()
                .filter(tarefa -> tarefa.getTitulo().equals("Meditação"))
                .min(Comparator.comparing(Tarefa::getData))
                .orElseThrow()
                .getId();

        var tarefa = Assertions.assertDoesNotThrow(() -> tarefaService.buscar(idTarefa));
        Assertions.assertEquals(Prazo.NAO_REALIZADA, tarefa.prazo());
    }

    @Test
    void salvarTest() {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Estudos")).getId();
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();

        var tarefaEnvioDTO = new TarefaEnvioDTO(
                "Aula de conversação",
                "Praticar o vocabulário da semana",
                LocalDate.now(),
                false,
                LocalTime.of(19, 0),
                LocalTime.of(20, 0),
                Prioridade.ALTA,
                Situacao.PENDENTE,
                idCategoria,
                idAtividade,
                30,
                new RecorrenciaDTO(Frequencia.DIAS_DA_SEMANA, List.of(DiaSemana.TERCA, DiaSemana.QUINTA), LocalDate.now().plusMonths(2)));

        var tarefa = Assertions.assertDoesNotThrow(() -> tarefaService.salvar(tarefaEnvioDTO));
        Assertions.assertNotNull(tarefa);
    }

    @Test
    void atualizarTest() {
        var tarefa = tarefaRepository.findAll()
                .stream()
                .filter(item -> item.getTitulo().equals("Meditação"))
                .sorted(Comparator.comparing(Tarefa::getData))
                .toList()
                .get(1);

        var idSerieAntiga = tarefa.getSerie().getId();

        var tarefaEnvioDTO = new TarefaEnvioDTO(
                "Meditação guiada",
                null,
                tarefa.getData(),
                false,
                LocalTime.of(6, 30),
                LocalTime.of(7, 0),
                Prioridade.ALTA,
                Situacao.PENDENTE,
                null,
                null,
                5,
                new RecorrenciaDTO(Frequencia.SEMANAL, null, null));

        var atualizada = Assertions.assertDoesNotThrow(() -> tarefaService.atualizar(tarefa.getId(), EscopoAlteracao.ESTA_E_PROXIMAS, tarefaEnvioDTO));
        Assertions.assertNotEquals(idSerieAntiga, atualizada.serieId());
    }

    @Test
    void alterarSituacaoTest() {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();

        var situacaoTarefaDTO = new SituacaoTarefaDTO(Situacao.CONCLUIDA);

        var tarefa = Assertions.assertDoesNotThrow(() -> tarefaService.alterarSituacao(idTarefa, situacaoTarefaDTO));
        Assertions.assertNotNull(tarefa.dataConclusao());
    }

    @Test
    void reagendarTest() {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();
        var idTarefaSemData = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Trocar a lâmpada")).getId();

        var reagendamentoDTO = new ReagendamentoDTO(List.of(
                new ItemReagendamentoDTO(idTarefa, LocalDate.now().plusDays(1)),
                new ItemReagendamentoDTO(idTarefaSemData, LocalDate.now())));

        var tarefas = Assertions.assertDoesNotThrow(() -> tarefaService.reagendar(reagendamentoDTO));
        Assertions.assertEquals(2, tarefas.size());
    }

    @Test
    void deletarTest() {
        var tarefa = tarefaRepository.findAll()
                .stream()
                .filter(item -> item.getTitulo().equals("Meditação"))
                .sorted(Comparator.comparing(Tarefa::getData))
                .toList()
                .get(1);

        Assertions.assertDoesNotThrow(() -> tarefaService.deletar(tarefa.getId(), EscopoAlteracao.ESTA_E_PROXIMAS));
        Assertions.assertEquals(tarefa.getData().minusDays(1), tarefa.getSerie().getDataFim());
    }
}