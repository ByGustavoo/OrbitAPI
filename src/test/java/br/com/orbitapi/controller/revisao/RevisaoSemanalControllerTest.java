package br.com.orbitapi.controller.revisao;

import br.com.orbitapi.config.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@SpringBootTest
class RevisaoSemanalControllerTest extends AbstractControllerTest {

    private String salvarNotaSemanaRequest;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarNotaSemanaRequest == null) {
            salvarNotaSemanaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/revisao/salvarNotaSemanaRequest.json")));
        }
    }

    @Test
    void buscarRevisaoSemanalTest() throws Exception {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        testGet("/v1/revisao-semanal?inicioSemana=" + inicioSemana);
    }

    @Test
    void salvarNotaSemanaTest() throws Exception {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        testPut("/v1/revisao-semanal/" + inicioSemana + "/nota", salvarNotaSemanaRequest);
    }
}