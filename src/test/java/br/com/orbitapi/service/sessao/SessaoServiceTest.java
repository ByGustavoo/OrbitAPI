package br.com.orbitapi.service.sessao;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.ModoCronometro;
import br.com.orbitapi.enums.OrigemSessao;
import br.com.orbitapi.model.dto.sessao.FiltroSessoesDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class SessaoServiceTest extends AbstractTest {

    @Autowired
    private SessaoService sessaoService;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Test
    void listarTest() {
        var filtro = new FiltroSessoesDTO(LocalDate.now().minusDays(6), LocalDate.now(), null, 0, 100);

        var pagina = Assertions.assertDoesNotThrow(() -> sessaoService.listar(filtro));
        Assertions.assertEquals(2, pagina.totalItens());
    }

    @Test
    void salvarTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();
        var idTarefa = buscar(tarefaRepository, tarefa -> tarefa.getTitulo().equals("Revisar verbos irregulares")).getId();

        var sessaoEnvioDTO = new SessaoEnvioDTO(
                idAtividade,
                idTarefa,
                ModoCronometro.LIVRE,
                OrigemSessao.MANUAL,
                Instant.now().minus(2, ChronoUnit.HOURS),
                Instant.now().minus(1, ChronoUnit.HOURS),
                2700,
                null,
                "Exercícios da unidade 4");

        var sessao = Assertions.assertDoesNotThrow(() -> sessaoService.salvar(sessaoEnvioDTO));
        Assertions.assertNotNull(sessao.tarefa());
    }

    @Test
    void atualizarTest() {
        var sessao = buscar(sessaoRepository, item -> item.getAtividade().getNome().equals("Inglês"));

        sessao.getAtividade().setArquivada(true);

        var sessaoEnvioDTO = new SessaoEnvioDTO(
                sessao.getAtividade().getId(),
                null,
                ModoCronometro.POMODORO,
                OrigemSessao.CRONOMETRO,
                sessao.getInicio(),
                sessao.getFim(),
                2400,
                3,
                "Revisei as lições 12 e 13");

        var atualizada = Assertions.assertDoesNotThrow(() -> sessaoService.atualizar(sessao.getId(), sessaoEnvioDTO));
        Assertions.assertNotNull(atualizada.tarefa());
    }

    @Test
    void deletarTest() {
        var idSessao = buscar(sessaoRepository, sessao -> sessao.getAtividade().getNome().equals("Leitura")).getId();

        Assertions.assertDoesNotThrow(() -> sessaoService.deletar(idSessao));
    }
}