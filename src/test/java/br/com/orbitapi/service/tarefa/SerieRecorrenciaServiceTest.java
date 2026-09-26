package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.Frequencia;
import br.com.orbitapi.model.dto.tarefa.RecorrenciaDTO;
import br.com.orbitapi.repository.tarefa.SerieRecorrenciaRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;

@SpringBootTest
class SerieRecorrenciaServiceTest extends AbstractTest {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private SerieRecorrenciaService serieRecorrenciaService;

    @Autowired
    private SerieRecorrenciaRepository serieRecorrenciaRepository;

    @Test
    void iniciarTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Pagar a conta de luz"));

        var recorrenciaDTO = new RecorrenciaDTO(Frequencia.MENSAL, null, null);

        Assertions.assertDoesNotThrow(() -> serieRecorrenciaService.iniciar(tarefa, recorrenciaDTO, Instant.now(), Set.of()));
        Assertions.assertNotNull(tarefa.getSerie());
    }

    @Test
    void estenderTest() {
        var serie = buscar(serieRecorrenciaRepository, item -> item.getGeradaAte() != null);
        var geradaAteAntes = serie.getGeradaAte();

        var estendeu = Assertions.assertDoesNotThrow(() -> serieRecorrenciaService.estender(Instant.now()));
        Assertions.assertTrue(estendeu);
        Assertions.assertTrue(serie.getGeradaAte().isAfter(geradaAteAntes));
    }

    @Test
    void encerrarAntesDeTest() {
        var tarefa = buscar(tarefaRepository, item -> item.getTitulo().equals("Meditação"));

        Assertions.assertDoesNotThrow(() -> serieRecorrenciaService.encerrarAntesDe(tarefa.getSerie(), tarefa.getSerie().getDataInicial()));
        Assertions.assertTrue(serieRecorrenciaRepository.findAll().isEmpty());
    }
}