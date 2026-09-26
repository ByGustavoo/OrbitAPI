package br.com.orbitapi.controller.estudo;

import br.com.orbitapi.config.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@SpringBootTest
class EstudoControllerTest extends AbstractControllerTest {

    @Test
    void buscarResumoEstudosTest() throws Exception {
        testGet("/v1/estudos/resumo?dataInicial=" + LocalDate.now().minusDays(6) + "&dataFinal=" + LocalDate.now());
    }

    @Test
    void buscarProgressoSemanalTest() throws Exception {
        testGet("/v1/estudos/progresso-semanal?inicioSemana=" + LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
    }

    @Test
    void buscarMapaCalorTest() throws Exception {
        testGet("/v1/estudos/mapa-calor?dataInicial=" + LocalDate.now().minusMonths(5).withDayOfMonth(1) + "&dataFinal=" + LocalDate.now());
    }
}