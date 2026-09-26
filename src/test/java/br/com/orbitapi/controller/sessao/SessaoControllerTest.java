package br.com.orbitapi.controller.sessao;

import br.com.orbitapi.config.AbstractControllerTest;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@SpringBootTest
class SessaoControllerTest extends AbstractControllerTest {

    private String salvarSessaoRequest;
    private String atualizarSessaoRequest;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarSessaoRequest == null) {
            salvarSessaoRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/sessao/salvarSessaoRequest.json")));
        }

        if (atualizarSessaoRequest == null) {
            atualizarSessaoRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/sessao/atualizarSessaoRequest.json")));
        }
    }

    @Test
    void listarSessoesTest() throws Exception {
        testGet("/v1/sessoes?dataInicial=" + LocalDate.now().minusDays(6) + "&dataFinal=" + LocalDate.now() + "&tamanho=100");
    }

    @Test
    void salvarSessaoTest() throws Exception {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Matemática")).getId();

        testPost("/v1/sessoes", salvarSessaoRequest.formatted(idAtividade));
    }

    @Test
    void atualizarSessaoTest() throws Exception {
        var idSessao = buscar(sessaoRepository, sessao -> sessao.getAtividade().getNome().equals("Leitura")).getId();
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Leitura")).getId();

        testPut("/v1/sessoes/" + idSessao, atualizarSessaoRequest.formatted(idAtividade));
    }

    @Test
    void deletarSessaoTest() throws Exception {
        var idSessao = buscar(sessaoRepository, sessao -> sessao.getAtividade().getNome().equals("Leitura")).getId();

        testDelete("/v1/sessoes/" + idSessao);
    }
}