package br.com.orbitapi.service.categoria;

import br.com.orbitapi.config.AbstractTest;
import br.com.orbitapi.enums.Cor;
import br.com.orbitapi.model.dto.categoria.CategoriaEnvioDTO;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CategoriaServiceTest extends AbstractTest {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void listarTest() {
        var categorias = Assertions.assertDoesNotThrow(() -> categoriaService.listar());
        Assertions.assertNotNull(categorias);
    }

    @Test
    void salvarTest() {
        var categoriaEnvioDTO = new CategoriaEnvioDTO(
                "Viagens",
                Cor.CIANO);

        var categoria = Assertions.assertDoesNotThrow(() -> categoriaService.salvar(categoriaEnvioDTO));
        Assertions.assertNotNull(categoria);
    }

    @Test
    void atualizarTest() {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Trabalho")).getId();

        var categoriaEnvioDTO = new CategoriaEnvioDTO(
                "Trabalho remoto",
                Cor.CIANO);

        var categoria = Assertions.assertDoesNotThrow(() -> categoriaService.atualizar(idCategoria, categoriaEnvioDTO));
        Assertions.assertNotNull(categoria);
    }

    @Test
    void deletarTest() {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Saúde")).getId();

        Assertions.assertDoesNotThrow(() -> categoriaService.deletar(idCategoria));
    }
}