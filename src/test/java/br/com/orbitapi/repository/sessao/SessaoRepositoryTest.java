package br.com.orbitapi.repository.sessao;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.ZoneId;

@SpringBootTest
class SessaoRepositoryTest extends AbstractTest {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Test
    void existsByAtividadeIdTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();

        var existe = Assertions.assertDoesNotThrow(() -> sessaoRepository.existsByAtividadeId(idAtividade));
        Assertions.assertTrue(existe);
    }

    @Test
    void buscarPaginaTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Leitura")).getId();

        var pagina = Assertions.assertDoesNotThrow(() -> sessaoRepository.buscarPagina(null, null, idAtividade, PageRequest.of(0, 20)));
        Assertions.assertEquals(1, pagina.getTotalElements());
    }

    @Test
    void somarPorDiaTest() {
        var dias = Assertions.assertDoesNotThrow(() -> sessaoRepository.somarPorDia(null, null, null, ZoneId.of("America/Sao_Paulo")));
        Assertions.assertFalse(dias.isEmpty());
    }

    @Test
    void somarPorAtividadeTest() {
        var atividades = Assertions.assertDoesNotThrow(() -> sessaoRepository.somarPorAtividade(null, null, null));
        Assertions.assertEquals(2, atividades.size());
    }

    @Test
    void somarMinutosPorAtividadeTest() {
        var minutos = Assertions.assertDoesNotThrow(() -> sessaoRepository.somarMinutosPorAtividade(null, null));
        Assertions.assertEquals(2, minutos.size());
    }

    @Test
    void somarMinutosPorDiaTest() {
        var dias = Assertions.assertDoesNotThrow(() -> sessaoRepository.somarMinutosPorDia(null, null, ZoneId.of("America/Sao_Paulo")));
        Assertions.assertFalse(dias.isEmpty());
    }

    @Test
    void somarMinutosESessoesPorAtividadeTest() {
        var atividades = Assertions.assertDoesNotThrow(() -> sessaoRepository.somarMinutosESessoesPorAtividade(null, null));
        Assertions.assertEquals(2, atividades.size());
    }
}