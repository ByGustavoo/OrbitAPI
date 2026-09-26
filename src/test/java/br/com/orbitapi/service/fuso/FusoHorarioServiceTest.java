package br.com.orbitapi.service.fuso;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@SpringBootTest
class FusoHorarioServiceTest extends AbstractTest {

    @Autowired
    private FusoHorarioService fusoHorarioService;

    @Test
    void obterTest() {
        var fuso = Assertions.assertDoesNotThrow(() -> fusoHorarioService.obter());
        Assertions.assertEquals(ZoneId.of("America/Sao_Paulo"), fuso);
    }

    @Test
    void inicioDoDiaTest() {
        var inicio = Assertions.assertDoesNotThrow(() -> fusoHorarioService.inicioDoDia(LocalDate.of(2026, 9, 25)));
        Assertions.assertEquals(Instant.parse("2026-09-25T03:00:00Z"), inicio);
    }
}