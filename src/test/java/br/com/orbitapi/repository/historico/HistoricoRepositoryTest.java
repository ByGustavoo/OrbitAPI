package br.com.orbitapi.repository.historico;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.AreaHistorico;
import br.com.orbitapi.model.dto.historico.FiltroHistoricoDTO;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class HistoricoRepositoryTest extends AbstractTest {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private HistoricoRepository historicoRepository;

    @Test
    void buscarPaginaTest() {
        var filtro = new FiltroHistoricoDTO(LocalDate.now().minusDays(7), LocalDate.now(), AreaHistorico.ESTUDOS, null, 0, 30);

        var registros = Assertions.assertDoesNotThrow(() -> historicoRepository.buscarPagina(filtro, Instant.now().minus(8, ChronoUnit.DAYS), Instant.now().plus(1, ChronoUnit.DAYS), ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(2, registros.size());
    }

    @Test
    void contarTest() {
        var filtro = new FiltroHistoricoDTO(LocalDate.now().minusDays(7), LocalDate.now(), AreaHistorico.TAREFAS, "conta", 0, 30);

        var quantidade = Assertions.assertDoesNotThrow(() -> historicoRepository.contar(filtro, Instant.now().minus(8, ChronoUnit.DAYS), Instant.now().plus(1, ChronoUnit.DAYS), ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertEquals(2, quantidade);
    }

    @Test
    void buscarRegistroTest() {
        var idSessao = buscar(sessaoRepository, sessao -> sessao.getAtividade().getNome().equals("Inglês")).getId();

        var registro = Assertions.assertDoesNotThrow(() -> historicoRepository.buscarRegistro("sessao", idSessao, ZoneId.of("America/Sao_Paulo"), Instant.now()));
        Assertions.assertTrue(registro.isPresent());
    }
}