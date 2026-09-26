package br.com.orbitapi.controller.categoria;

import br.com.orbitapi.config.AbstractControllerTest;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class CategoriaControllerTest extends AbstractControllerTest {

    private String salvarCategoriaRequest;
    private String atualizarCategoriaRequest;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() throws IOException {
        if (salvarCategoriaRequest == null) {
            salvarCategoriaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/categoria/salvarCategoriaRequest.json")));
        }

        if (atualizarCategoriaRequest == null) {
            atualizarCategoriaRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requests/categoria/atualizarCategoriaRequest.json")));
        }
    }

    @Test
    void listarCategoriasTest() throws Exception {
        testGet("/v1/categorias");
    }

    @Test
    void salvarCategoriaTest() throws Exception {
        testPost("/v1/categorias", salvarCategoriaRequest);
    }

    @Test
    void atualizarCategoriaTest() throws Exception {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Trabalho")).getId();

        testPut("/v1/categorias/" + idCategoria, atualizarCategoriaRequest);
    }

    @Test
    void deletarCategoriaTest() throws Exception {
        var idCategoria = buscar(categoriaRepository, categoria -> categoria.getNome().equals("Saúde")).getId();

        testDelete("/v1/categorias/" + idCategoria);
    }
}