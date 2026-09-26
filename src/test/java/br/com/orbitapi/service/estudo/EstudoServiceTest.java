package br.com.orbitapi.service.estudo;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class EstudoServiceTest extends AbstractTest {

    @Autowired
    private EstudoService estudoService;

    @Test
    void resumirTest() {
        var resumo = Assertions.assertDoesNotThrow(() -> estudoService.resumir(LocalDate.now().minusDays(6), LocalDate.now(), null));

        Assertions.assertEquals(2, resumo.totalSessoes());
        Assertions.assertEquals(7, resumo.porDia().size());
    }

    @Test
    void buscarProgressoSemanalTest() {
        var progresso = Assertions.assertDoesNotThrow(() -> estudoService.buscarProgressoSemanal(LocalDate.now().minusDays(6)));

        Assertions.assertEquals(3, progresso.size());
        Assertions.assertEquals(50, progresso.getFirst().minutosRealizados());
    }

    @Test
    void gerarMapaCalorTest() {
        var mapa = Assertions.assertDoesNotThrow(() -> estudoService.gerarMapaCalor(LocalDate.now().minusDays(30), LocalDate.now()));

        Assertions.assertEquals(31, mapa.dias().size());
        Assertions.assertEquals("Leitura", mapa.atividadeMaisEstudada().nome());
    }
}