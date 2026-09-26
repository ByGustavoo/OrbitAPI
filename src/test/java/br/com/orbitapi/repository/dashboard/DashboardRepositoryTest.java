package br.com.orbitapi.repository.dashboard;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.TipoEventoRecente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@SpringBootTest
class DashboardRepositoryTest extends AbstractTest {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Test
    void buscarEventosRecentesTest() {
        var eventos = Assertions.assertDoesNotThrow(() -> dashboardRepository.buscarEventosRecentes(Instant.now(), 6));
        Assertions.assertEquals(3, eventos.size());
        Assertions.assertEquals(TipoEventoRecente.TAREFA_CRIADA, eventos.getFirst().tipo());
    }

    @Test
    void listarDiasComAtividadeTest() {
        var dias = Assertions.assertDoesNotThrow(() -> dashboardRepository.listarDiasComAtividade(LocalDate.now().plusDays(1), ZoneId.of("America/Sao_Paulo")));
        Assertions.assertEquals(2, dias.size());
    }
}