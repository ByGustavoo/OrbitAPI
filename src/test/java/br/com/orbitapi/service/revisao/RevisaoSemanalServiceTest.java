package br.com.orbitapi.service.revisao;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.exceptions.SemanaInvalidaException;
import br.com.orbitapi.model.dto.revisao.NotaSemanaEnvioDTO;
import br.com.orbitapi.repository.revisao.NotaSemanaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

@SpringBootTest
class RevisaoSemanalServiceTest extends AbstractTest {

    @Autowired
    private NotaSemanaRepository notaSemanaRepository;

    @Autowired
    private RevisaoSemanalService revisaoSemanalService;

    @Test
    void buscarTest() {
        var inicioSemana = LocalDate.now(ZoneId.of("America/Sao_Paulo")).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        var revisao = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.buscar(inicioSemana));
        Assertions.assertTrue(revisao.emAndamento());
        Assertions.assertEquals(7, revisao.porDia().size());
        Assertions.assertFalse(revisao.tarefas().pendentes().isEmpty());
        Assertions.assertEquals(2, revisao.resumo().sessoes() + revisao.semanaAnterior().sessoes());
        Assertions.assertEquals(1, revisao.resumo().criadas() + revisao.semanaAnterior().criadas());
        Assertions.assertEquals(7, revisao.proximaSemana().agendadas());
        Assertions.assertEquals(1, revisao.proximaSemana().atrasadasEmAberto());
    }

    @Test
    void buscarSemanaFuturaTest() {
        var inicioSemana = LocalDate.now(ZoneId.of("America/Sao_Paulo")).with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1);

        var revisao = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.buscar(inicioSemana));
        Assertions.assertEquals(0, revisao.diasDecorridos());
        Assertions.assertEquals(0, revisao.resumo().planejadas());
        Assertions.assertNull(revisao.resumo().taxaConclusao());
        Assertions.assertEquals(7, revisao.tarefas().planejadas());
    }

    @Test
    void buscarComNotaTest() {
        var existente = notaSemanaRepository.findAll().getFirst();

        var revisao = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.buscar(existente.getInicioSemana()));
        Assertions.assertNotNull(revisao.nota());
    }

    @Test
    void buscarForaDoDomingoTest() {
        var segunda = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Assertions.assertThrows(SemanaInvalidaException.class, () -> revisaoSemanalService.buscar(segunda));
    }

    @Test
    void salvarNotaTest() {
        var inicioSemana = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusWeeks(1);

        var nota = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.salvarNota(inicioSemana, new NotaSemanaEnvioDTO("  Planejar a viagem.  ")));
        Assertions.assertEquals("Planejar a viagem.", nota.orElseThrow().texto());
        Assertions.assertTrue(notaSemanaRepository.existsById(inicioSemana));
    }

    @Test
    void salvarNotaExistenteTest() {
        var existente = notaSemanaRepository.findAll().getFirst();

        var nota = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.salvarNota(existente.getInicioSemana(), new NotaSemanaEnvioDTO("Semana leve.")));
        Assertions.assertEquals("Semana leve.", nota.orElseThrow().texto());
        Assertions.assertEquals(1, notaSemanaRepository.count());
    }

    @Test
    void apagarNotaTest() {
        var existente = notaSemanaRepository.findAll().getFirst();

        var nota = Assertions.assertDoesNotThrow(() -> revisaoSemanalService.salvarNota(existente.getInicioSemana(), new NotaSemanaEnvioDTO("   ")));
        Assertions.assertTrue(nota.isEmpty());
        Assertions.assertFalse(notaSemanaRepository.existsById(existente.getInicioSemana()));
    }

    @Test
    void salvarNotaForaDoDomingoTest() {
        var segunda = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Assertions.assertThrows(SemanaInvalidaException.class, () -> revisaoSemanalService.salvarNota(segunda, new NotaSemanaEnvioDTO("Texto")));
    }
}