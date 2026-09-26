package br.com.orbitapi.controller.historico;

import br.com.orbitapi.config.AbstractControllerTest;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class HistoricoControllerTest extends AbstractControllerTest {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Test
    void listarHistoricoTest() throws Exception {
        testGet("/v1/historico?dataInicial=" + LocalDate.now().minusDays(7) + "&dataFinal=" + LocalDate.now() + "&area=TAREFAS&busca=conta");
    }

    @Test
    void buscarRegistroHistoricoTest() throws Exception {
        var idSessao = buscar(sessaoRepository, sessao -> sessao.getAtividade().getNome().equals("Leitura")).getId();

        testGet("/v1/historico/sessao-" + idSessao);
    }
}