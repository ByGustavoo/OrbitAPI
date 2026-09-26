package br.com.orbitapi.service.atividade;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.Cor;
import br.com.orbitapi.model.dto.atividade.ArquivamentoAtividadeDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEnvioDTO;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AtividadeServiceTest extends AbstractTest {

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Test
    void listarTest() {
        var atividades = Assertions.assertDoesNotThrow(() -> atividadeService.listar());
        Assertions.assertNotNull(atividades);
    }

    @Test
    void salvarTest() {
        var atividadeEnvioDTO = new AtividadeEnvioDTO(
                "Redação",
                Cor.ROSA,
                120);

        var atividade = Assertions.assertDoesNotThrow(() -> atividadeService.salvar(atividadeEnvioDTO));
        Assertions.assertNotNull(atividade);
    }

    @Test
    void atualizarTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Inglês")).getId();

        var atividadeEnvioDTO = new AtividadeEnvioDTO(
                "Inglês avançado",
                Cor.CIANO,
                360);

        var atividade = Assertions.assertDoesNotThrow(() -> atividadeService.atualizar(idAtividade, atividadeEnvioDTO));
        Assertions.assertNotNull(atividade);
    }

    @Test
    void alterarArquivamentoTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Francês")).getId();

        var arquivamentoAtividadeDTO = new ArquivamentoAtividadeDTO(false);

        var atividade = Assertions.assertDoesNotThrow(() -> atividadeService.alterarArquivamento(idAtividade, arquivamentoAtividadeDTO));
        Assertions.assertNotNull(atividade);
    }

    @Test
    void deletarTest() {
        var idAtividade = buscar(atividadeRepository, atividade -> atividade.getNome().equals("Violão")).getId();

        Assertions.assertDoesNotThrow(() -> atividadeService.deletar(idAtividade));
    }
}