package br.com.orbitapi.service.dashboard;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.exceptions.PeriodoInvalidoException;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;

@SpringBootTest
class DashboardServiceTest extends AbstractTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Test
    void resumirTest() {
        var hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        var resumo = Assertions.assertDoesNotThrow(() -> dashboardService.resumir(hoje.minusDays(6), hoje));
        Assertions.assertEquals(7, resumo.concluidasPorDia().size());
        Assertions.assertEquals(7, resumo.minutosEstudoPorDia().size());
        Assertions.assertEquals(110, resumo.minutosEstudoPorDia().stream().mapToLong(dia -> dia.minutos()).sum());
        Assertions.assertEquals(Prioridade.BAIXA, resumo.distribuicaoPrioridade().getFirst().prioridade());
        Assertions.assertEquals(1, resumo.contagens().atrasadas());
        Assertions.assertEquals(3, resumo.eventosRecentes().size());
    }

    @Test
    void resumirPeriodoInvertidoTest() {
        var hoje = LocalDate.now();

        Assertions.assertThrows(PeriodoInvalidoException.class, () -> dashboardService.resumir(hoje, hoje.minusDays(1)));
    }

    @Test
    void buscarSequenciaTest() {
        var sessao = buscar(sessaoRepository, item -> item.getAtividade().getNome().equals("Leitura"));
        var dia = LocalDate.ofInstant(sessao.getInicio(), ZoneId.of("America/Sao_Paulo"));

        var sequencia = Assertions.assertDoesNotThrow(() -> dashboardService.buscarSequencia(dia));
        Assertions.assertTrue(sequencia.contaHoje());
        Assertions.assertTrue(sequencia.atual() >= 1);
        Assertions.assertTrue(sequencia.recorde() >= sequencia.atual());
    }

    @Test
    void buscarSequenciaSemAtividadeTest() {
        var sequencia = Assertions.assertDoesNotThrow(() -> dashboardService.buscarSequencia(LocalDate.now().minusYears(1)));
        Assertions.assertEquals(0, sequencia.atual());
        Assertions.assertEquals(0, sequencia.recorde());
        Assertions.assertFalse(sequencia.contaHoje());
    }
}