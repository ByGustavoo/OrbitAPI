package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class EventoTarefaServiceTest extends AbstractTest {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private EventoTarefaService eventoTarefaService;

    @Test
    void registrarTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Pagar a conta de luz"));

        Assertions.assertDoesNotThrow(() -> eventoTarefaService.registrar(tarefa, TipoEventoTarefa.TAREFA_CRIADA, Instant.now()));
    }

    @Test
    void registrarAlteracaoTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Pagar a conta de luz"));

        Assertions.assertDoesNotThrow(() -> eventoTarefaService.registrarAlteracao(tarefa, TipoEventoTarefa.TAREFA_REABERTA, Instant.now(), "CONCLUIDA", "PENDENTE"));
    }
}