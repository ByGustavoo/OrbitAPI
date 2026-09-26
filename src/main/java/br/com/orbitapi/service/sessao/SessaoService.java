package br.com.orbitapi.service.sessao;

import br.com.orbitapi.exceptions.AtividadeIndisponivelException;
import br.com.orbitapi.enums.ModoCronometro;
import br.com.orbitapi.exceptions.PeriodoInvalidoException;
import br.com.orbitapi.exceptions.SessaoNaoEncontradaException;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.sessao.FiltroSessoesDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.entity.sessao.SessaoEstudo;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.model.mapper.sessao.SessaoMapper;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoMapper sessaoMapper;
    private final SessaoRepository sessaoRepository;
    private final TarefaRepository tarefaRepository;
    private final FusoHorarioService fusoHorarioService;
    private final AtividadeRepository atividadeRepository;
    private static final Sort MAIS_RECENTES_PRIMEIRO = Sort.by(Sort.Order.desc("inicio"), Sort.Order.desc("id"));

    @Transactional(readOnly = true)
    public PaginaDTO<SessaoEstudoDTO> listar(FiltroSessoesDTO filtro) {
        log.info("Listando as sessões de estudo... - Filtro: {}", filtro);

        if (filtro.dataInicial() != null && filtro.dataFinal() != null && filtro.dataInicial().isAfter(filtro.dataFinal())) {
            throw new PeriodoInvalidoException("A data inicial precisa ser antes da final!");
        }

        var pagina = sessaoRepository.buscarPagina(
                fusoHorarioService.inicioDoDia(filtro.dataInicial()),
                fusoHorarioService.inicioDoDia(filtro.dataFinal() == null ? null : filtro.dataFinal().plusDays(1)),
                filtro.atividadeId(),
                PageRequest.of(filtro.pagina(), filtro.tamanho(), MAIS_RECENTES_PRIMEIRO));

        return new PaginaDTO<>(pagina.map(sessaoMapper::toDTO).getContent(), filtro.pagina(), filtro.tamanho(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    @Transactional
    public SessaoEstudoDTO salvar(SessaoEnvioDTO sessaoEnvioDTO) {
        log.info("Salvando a sessão de estudo... - Atividade: [{}] - Início: {}", sessaoEnvioDTO.atividadeId(), sessaoEnvioDTO.inicio());
        var sessao = sessaoMapper.toEntity(sessaoEnvioDTO);

        sessao.setAtividade(buscarAtividade(sessaoEnvioDTO.atividadeId(), null));
        sessao.setTarefa(buscarTarefa(sessaoEnvioDTO.tarefaId()));
        sessaoRepository.save(sessao);

        return sessaoMapper.toDTO(sessao);
    }

    @Transactional
    public SessaoEstudoDTO atualizar(Long id, SessaoEnvioDTO sessaoEnvioDTO) {
        log.info("Atualizando a sessão de estudo... - ID: [{}]", id);
        var sessao = buscar(id);

        sessao.setAtividade(buscarAtividade(sessaoEnvioDTO.atividadeId(), sessao.getAtividade().getId()));
        sessaoMapper.updateEntity(sessaoEnvioDTO, sessao);
        sessao.setCiclosConcluidos(sessao.getModo() == ModoCronometro.POMODORO ? sessaoEnvioDTO.ciclosConcluidos() : null);

        return sessaoMapper.toDTO(sessao);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Excluindo a sessão de estudo... - ID: [{}]", id);
        sessaoRepository.delete(buscar(id));
    }

    private SessaoEstudo buscar(Long id) {
        return sessaoRepository.findById(id)
                .orElseThrow(() -> new SessaoNaoEncontradaException("Esta sessão não existe mais!"));
    }

    private AtividadeEstudo buscarAtividade(Long id, Long idAtual) {
        var atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new AtividadeIndisponivelException("Esta atividade não existe mais. Escolha outra!"));

        if (atividade.isArquivada() && !atividade.getId().equals(idAtual)) {
            throw new AtividadeIndisponivelException("Esta atividade está arquivada. Escolha outra ou desarquive-a!");
        }

        return atividade;
    }

    private Tarefa buscarTarefa(Long id) {
        return id == null ? null : tarefaRepository.findById(id).orElse(null);
    }
}