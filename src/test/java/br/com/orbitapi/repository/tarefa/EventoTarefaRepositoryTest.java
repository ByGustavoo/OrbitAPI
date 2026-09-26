package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.TipoEventoTarefa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class EventoTarefaRepositoryTest extends AbstractTest {

    @Autowired
    private EventoTarefaRepository eventoTarefaRepository;

    @Test
    void contarNoPeriodoTest() {
        var agora = Instant.now();

        var quantidade = Assertions.assertDoesNotThrow(() -> eventoTarefaRepository.contarNoPeriodo(TipoEventoTarefa.TAREFA_CRIADA, agora.minus(2, ChronoUnit.DAYS), agora.plus(1, ChronoUnit.DAYS), agora));
        Assertions.assertEquals(1, quantidade);
    }
}