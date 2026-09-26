package br.com.orbitapi.controller.dashboard;

import br.com.orbitapi.config.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class DashboardControllerTest extends AbstractControllerTest {

    @Test
    void buscarResumoDashboardTest() throws Exception {
        testGet("/v1/dashboard/resumo?dataInicial=" + LocalDate.now().minusDays(6) + "&dataFinal=" + LocalDate.now());
    }

    @Test
    void buscarSequenciaTest() throws Exception {
        testGet("/v1/dashboard/sequencia?data=" + LocalDate.now());
    }
}