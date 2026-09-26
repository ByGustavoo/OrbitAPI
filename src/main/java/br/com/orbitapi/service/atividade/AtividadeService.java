package br.com.orbitapi.service.atividade;

import br.com.orbitapi.exceptions.AtividadeComSessoesException;
import br.com.orbitapi.exceptions.AtividadeDuplicadaException;
import br.com.orbitapi.exceptions.AtividadeNaoEncontradaException;
import br.com.orbitapi.model.dto.atividade.ArquivamentoAtividadeDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEnvioDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEstudoDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.mapper.atividade.AtividadeMapper;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class AtividadeService {

    private final AtividadeMapper atividadeMapper;
    private final SessaoRepository sessaoRepository;
    private final AtividadeRepository atividadeRepository;
    private static final Collator ORDEM_ALFABETICA = Collator.getInstance(Locale.forLanguageTag("pt-BR"));

    @Cacheable("atividades")
    @Transactional(readOnly = true)
    public List<AtividadeEstudoDTO> listar() {
        log.info("Listando as atividades de estudo...");

        return atividadeRepository.findAll()
                .stream()
                .map(atividadeMapper::toDTO)
                .sorted(Comparator.comparing(AtividadeEstudoDTO::nome, ORDEM_ALFABETICA))
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"atividades", "estudos", "sessoes"}, allEntries = true)
    public AtividadeEstudoDTO salvar(AtividadeEnvioDTO atividadeEnvioDTO) {
        log.info("Salvando a atividade de estudo... - Nome: {}", atividadeEnvioDTO.nome());
        validarNomeUnico(atividadeEnvioDTO.nome(), null);

        var atividade = atividadeRepository.save(atividadeMapper.toEntity(atividadeEnvioDTO));

        return atividadeMapper.toDTO(atividade);
    }

    @Transactional
    @CacheEvict(value = {"atividades", "estudos", "sessoes"}, allEntries = true)
    public AtividadeEstudoDTO atualizar(Long id, AtividadeEnvioDTO atividadeEnvioDTO) {
        log.info("Atualizando a atividade de estudo... - ID: [{}]", id);
        var atividade = buscar(id);

        validarNomeUnico(atividadeEnvioDTO.nome(), id);
        atividadeMapper.updateEntity(atividadeEnvioDTO, atividade);

        return atividadeMapper.toDTO(atividade);
    }

    @Transactional
    @CacheEvict(value = {"atividades", "estudos", "sessoes"}, allEntries = true)
    public AtividadeEstudoDTO alterarArquivamento(Long id, ArquivamentoAtividadeDTO arquivamentoAtividadeDTO) {
        log.info("Alterando o arquivamento da atividade de estudo... - ID: [{}] - Arquivada: {}", id, arquivamentoAtividadeDTO.arquivada());
        var atividade = buscar(id);

        if (!arquivamentoAtividadeDTO.arquivada()) {
            validarDesarquivamento(atividade);
        }

        atividade.setArquivada(arquivamentoAtividadeDTO.arquivada());

        return atividadeMapper.toDTO(atividade);
    }

    @Transactional
    @CacheEvict(value = {"atividades", "estudos", "sessoes"}, allEntries = true)
    public void deletar(Long id) {
        log.info("Excluindo a atividade de estudo... - ID: [{}]", id);
        var atividade = buscar(id);

        if (sessaoRepository.existsByAtividadeId(id)) {
            throw new AtividadeComSessoesException("Esta atividade tem sessões registradas. Arquive-a para manter o histórico!");
        }

        atividadeRepository.delete(atividade);
    }

    private AtividadeEstudo buscar(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new AtividadeNaoEncontradaException("Esta atividade não existe mais!"));
    }

    private void validarNomeUnico(String nome, Long idAtual) {
        atividadeRepository.findByNomeIgnoreCaseAndArquivadaFalse(nome)
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(existente -> {
                    throw new AtividadeDuplicadaException("Já existe uma atividade chamada “%s”. Escolha outro nome!".formatted(existente.getNome()));
                });
    }

    private void validarDesarquivamento(AtividadeEstudo atividade) {
        atividadeRepository.findByNomeIgnoreCaseAndArquivadaFalse(atividade.getNome())
                .filter(existente -> !existente.getId().equals(atividade.getId()))
                .ifPresent(existente -> {
                    throw new AtividadeDuplicadaException("Já existe uma atividade ativa chamada “%s”. Renomeie uma delas antes de desarquivar!".formatted(atividade.getNome()));
                });
    }
}