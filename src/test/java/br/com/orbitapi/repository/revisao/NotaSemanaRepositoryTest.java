package br.com.orbitapi.repository.revisao;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;

@SpringBootTest
class NotaSemanaRepositoryTest extends AbstractTest {

    @Autowired
    private NotaSemanaRepository notaSemanaRepository;

    @Test
    void findAllTest() {
        var notas = Assertions.assertDoesNotThrow(() -> notaSemanaRepository.findAll());
        Assertions.assertEquals(1, notas.size());
        Assertions.assertEquals(DayOfWeek.SUNDAY, notas.getFirst().getInicioSemana().getDayOfWeek());
    }
}