package br.com.orbitapi.controller.atividade;

import br.com.orbitapi.config.AbstractControllerTest;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class AtividadeControllerTest extends AbstractControllerTest {

    private String salvarAtividadeRequest;
    private String atualizarAtividadeRequest;
    private String alterarArquivamentoAtividadeRequest;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarAtividadeRequest == null) {
            salvarAtividadeRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/atividade/salvarAtividadeRequest.json")));
        }

        if (atualizarAtividadeRequest == null) {
            atualizarAtividadeRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/atividade/atualizarAtividadeRequest.json")));
        }

        if (alterarArquivamentoAtividadeRequest == null) {
            alterarArquivamentoAtividadeRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/atividade/alterarArquivamentoAtividadeRequest.json")));
        }
    }

    @Test
    void listarAtividadesTest() throws Exception {
        testGet("/v1/atividades");
    }

    @Test
    void salvarAtividadeTest() throws Exception {
        testPost("/v1/atividades", salvarAtividadeRequest);
    }

    @Test
    void atualizarAtividadeTest() throws Exception {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();

        testPut("/v1/atividades/" + idAtividade, atualizarAtividadeRequest);
    }

    @Test
    void alterarArquivamentoAtividadeTest() throws Exception {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();

        testPatch("/v1/atividades/" + idAtividade + "/arquivamento", alterarArquivamentoAtividadeRequest);
    }

    @Test
    void deletarAtividadeTest() throws Exception {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Violão")).getId();

        testDelete("/v1/atividades/" + idAtividade);
    }
}