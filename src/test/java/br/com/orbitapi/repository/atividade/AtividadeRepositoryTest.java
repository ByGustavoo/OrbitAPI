package br.com.orbitapi.repository.atividade;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AtividadeRepositoryTest extends AbstractTest {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Test
    void findByNomeIgnoreCaseAndArquivadaFalseTest() {
        Assertions.assertDoesNotThrow(() -> atividadeRepository.findByNomeIgnoreCaseAndArquivadaFalse("inglês"));
    }

    @Test
    void findByArquivadaFalseAndMetaSemanalMinutosNotNullTest() {
        var atividades = Assertions.assertDoesNotThrow(() -> atividadeRepository.findByArquivadaFalseAndMetaSemanalMinutosNotNull());
        Assertions.assertEquals(3, atividades.size());
    }
}