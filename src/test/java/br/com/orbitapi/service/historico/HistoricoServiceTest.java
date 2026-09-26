package br.com.orbitapi.service.historico;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.model.dto.historico.FiltroHistoricoDTO;
import br.com.orbitapi.repository.tarefa.EventoTarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class HistoricoServiceTest extends AbstractTest {

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private EventoTarefaRepository eventoTarefaRepository;

    @Test
    void listarTest() {
        var filtro = new FiltroHistoricoDTO(LocalDate.now().minusDays(7), LocalDate.now(), null, null, 0, 30);

        var pagina = Assertions.assertDoesNotThrow(() -> historicoService.listar(filtro));
        Assertions.assertTrue(pagina.totalItens() >= 5);
    }

    @Test
    void buscarTest() {
        var idEvento = buscar(eventoTarefaRepository, evento -> evento.getTipo() == TipoEventoTarefa.PRIORIDADE_ALTERADA).getId();

        var detalhe = Assertions.assertDoesNotThrow(() -> historicoService.buscar("evento-" + idEvento));
        Assertions.assertNotNull(detalhe.tarefa());
        Assertions.assertEquals("ALTA", detalhe.registro().alteracao().novo());
    }
}