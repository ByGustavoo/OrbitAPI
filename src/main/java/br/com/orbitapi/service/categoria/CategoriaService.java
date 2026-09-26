package br.com.orbitapi.service.categoria;

import br.com.orbitapi.exceptions.CategoriaDuplicadaException;
import br.com.orbitapi.exceptions.CategoriaNaoEncontradaException;
import br.com.orbitapi.model.dto.categoria.CategoriaDTO;
import br.com.orbitapi.model.dto.categoria.CategoriaEnvioDTO;
import br.com.orbitapi.model.dto.categoria.QuantidadeTarefasCategoriaDTO;
import br.com.orbitapi.model.entity.categoria.Categoria;
import br.com.orbitapi.model.mapper.categoria.CategoriaMapper;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaMapper categoriaMapper;
    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;
    private static final Collator ORDEM_ALFABETICA = Collator.getInstance(Locale.forLanguageTag("pt-BR"));

    @Cacheable("categorias")
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        log.info("Listando as categorias...");
        var quantidades = tarefaRepository.contarPorCategoria()
                .stream()
                .collect(Collectors.toMap(QuantidadeTarefasCategoriaDTO::categoriaId, QuantidadeTarefasCategoriaDTO::quantidade));

        return categoriaRepository.findAll()
                .stream()
                .map(categoria -> categoriaMapper.toDTO(categoria, quantidades.getOrDefault(categoria.getId(), 0L)))
                .sorted(Comparator.comparing(CategoriaDTO::nome, ORDEM_ALFABETICA))
                .toList();
    }

    @Transactional
    @CacheEvict(value = "categorias", allEntries = true)
    public CategoriaDTO salvar(CategoriaEnvioDTO categoriaEnvioDTO) {
        log.info("Salvando a categoria... - Nome: {}", categoriaEnvioDTO.nome());
        validarNomeUnico(categoriaEnvioDTO.nome(), null);

        var categoria = categoriaRepository.save(categoriaMapper.toEntity(categoriaEnvioDTO));

        return categoriaMapper.toDTO(categoria, 0);
    }

    @Transactional
    @CacheEvict(value = "categorias", allEntries = true)
    public CategoriaDTO atualizar(Long id, CategoriaEnvioDTO categoriaEnvioDTO) {
        log.info("Atualizando a categoria... - ID: [{}]", id);
        var categoria = buscar(id);

        validarNomeUnico(categoriaEnvioDTO.nome(), id);
        categoriaMapper.updateEntity(categoriaEnvioDTO, categoria);

        return categoriaMapper.toDTO(categoria, tarefaRepository.countByCategoriaId(id));
    }

    @Transactional
    @CacheEvict(value = "categorias", allEntries = true)
    public void deletar(Long id) {
        log.info("Excluindo a categoria... - ID: [{}]", id);
        categoriaRepository.delete(buscar(id));
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Esta categoria não existe mais!"));
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        categoriaRepository.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new CategoriaDuplicadaException("Já existe uma categoria chamada “%s”. Escolha outro nome!".formatted(existente.getNome()));
                });
    }
}