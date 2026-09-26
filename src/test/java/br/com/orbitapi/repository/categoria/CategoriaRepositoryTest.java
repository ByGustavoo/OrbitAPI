package br.com.orbitapi.repository.categoria;

import br.com.orbitapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoriaRepositoryTest extends AbstractTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void findByNomeIgnoreCaseTest() {
        Assertions.assertDoesNotThrow(() -> categoriaRepository.findByNomeIgnoreCase("saúde"));
    }
}