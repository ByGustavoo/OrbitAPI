package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class SerieRecorrenciaRepositoryTest extends AbstractTest {

    @Autowired
    private SerieRecorrenciaRepository serieRecorrenciaRepository;

    @Test
    void buscarParaEstenderTest() {
        var series = Assertions.assertDoesNotThrow(() -> serieRecorrenciaRepository.buscarParaEstender(LocalDate.now().plusMonths(3)));
        Assertions.assertEquals(1, series.size());
    }
}