package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
class PrazoServiceTest extends AbstractTest {

    @Autowired
    private PrazoService prazoService;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Test
    void calcularTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Trocar a lâmpada"));

        var prazo = Assertions.assertDoesNotThrow(() -> prazoService.calcular(tarefa, Instant.now()));
        Assertions.assertEquals(Prazo.SEM_DATA, prazo);
    }
}