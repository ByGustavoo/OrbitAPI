package br.com.orbitapi.controller.tarefa;

import br.com.orbitapi.config.AbstractControllerTest;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class TarefaControllerTest extends AbstractControllerTest {

    private String salvarTarefaRequest;
    private String atualizarTarefaRequest;
    private String reagendarTarefasRequest;
    private String alterarSituacaoTarefaRequest;

    @Autowired
    private TarefaRepository tarefaRepository;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarTarefaRequest == null) {
            salvarTarefaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/tarefa/salvarTarefaRequest.json")));
        }

        if (atualizarTarefaRequest == null) {
            atualizarTarefaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/tarefa/atualizarTarefaRequest.json")));
        }

        if (reagendarTarefasRequest == null) {
            reagendarTarefasRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/tarefa/reagendarTarefasRequest.json")));
        }

        if (alterarSituacaoTarefaRequest == null) {
            alterarSituacaoTarefaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/tarefa/alterarSituacaoTarefaRequest.json")));
        }
    }

    @Test
    void listarTarefasTest() throws Exception {
        testGet("/v1/tarefas?situacao=PENDENTE&situacao=EM_ANDAMENTO&ordenacao=PRIORIDADE&tamanho=10");
    }

    @Test
    void buscarResumoCalendarioTest() throws Exception {
        testGet("/v1/tarefas/resumo-calendario?dataInicial=" + LocalDate.now().minusDays(7) + "&dataFinal=" + LocalDate.now().plusDays(35));
    }

    @Test
    void buscarTarefaTest() throws Exception {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();

        testGet("/v1/tarefas/" + idTarefa);
    }

    @Test
    void salvarTarefaTest() throws Exception {
        testPost("/v1/tarefas", salvarTarefaRequest);
    }

    @Test
    void atualizarTarefaTest() throws Exception {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();

        testPut("/v1/tarefas/" + idTarefa + "?escopo=SOMENTE_ESTA", atualizarTarefaRequest);
    }

    @Test
    void alterarSituacaoTarefaTest() throws Exception {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();

        testPatch("/v1/tarefas/" + idTarefa + "/situacao", alterarSituacaoTarefaRequest);
    }

    @Test
    void reagendarTarefasTest() throws Exception {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Pagar a conta de luz")).getId();

        testPostOk("/v1/tarefas/reagendamentos", reagendarTarefasRequest.formatted(idTarefa));
    }

    @Test
    void deletarTarefaTest() throws Exception {
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Trocar a lâmpada")).getId();

        testDelete("/v1/tarefas/" + idTarefa + "?escopo=SOMENTE_ESTA");
    }
}