package br.com.orbitapi.controller.categoria;

import br.com.orbitapi.model.dto.categoria.CategoriaDTO;
import br.com.orbitapi.model.dto.categoria.CategoriaEnvioDTO;
import br.com.orbitapi.service.categoria.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categorias")
public class CategoriaController implements CategoriaDocs {

    private final CategoriaService categoriaService;

    @Override
    public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    @Override
    public ResponseEntity<CategoriaDTO> salvarCategoria(CategoriaEnvioDTO categoriaEnvioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.salvar(categoriaEnvioDTO));
    }

    @Override
    public ResponseEntity<CategoriaDTO> atualizarCategoria(Long id, CategoriaEnvioDTO categoriaEnvioDTO) {
        return ResponseEntity.ok(categoriaService.atualizar(id, categoriaEnvioDTO));
    }

    @Override
    public ResponseEntity<Void> deletarCategoria(Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}