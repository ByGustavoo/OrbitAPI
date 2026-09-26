package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.enums.EscopoAlteracao;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.exceptions.AlteracaoRecorrenciaException;
import br.com.orbitapi.exceptions.AtividadeIndisponivelException;
import br.com.orbitapi.exceptions.CategoriaIndisponivelException;
import br.com.orbitapi.exceptions.TarefaNaoEncontradaException;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.tarefa.AlteracaoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.DiaCalendarioDTO;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.model.dto.tarefa.ItemReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.PrazoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.ReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.SituacaoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import br.com.orbitapi.model.entity.atividade.AtividadeEstudo;
import br.com.orbitapi.model.entity.categoria.Categoria;
import br.com.orbitapi.model.mapper.tarefa.TarefaMapper;
import br.com.orbitapi.repository.atividade.AtividadeRepository;
import br.com.orbitapi.repository.categoria.CategoriaRepository;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class TarefaService {

    private final Clock clock;
    private final TarefaMapper tarefaMapper;
    private final PrazoService prazoService;
    private final TarefaRepository tarefaRepository;
    private final FusoHorarioService fusoHorarioService;
    private final EventoTarefaService eventoTarefaService;
    private final AtividadeRepository atividadeRepository;
    private final CategoriaRepository categoriaRepository;
    private final SerieRecorrenciaService serieRecorrenciaService;
    private static final List<Situacao> EM_ABERTO = List.of(Situacao.PENDENTE, Situacao.EM_ANDAMENTO);

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true)
    public TarefaDTO salvar(TarefaEnvioDTO tarefaEnvioDTO) {
        log.info("Salvando a tarefa... - Título: {}", tarefaEnvioDTO.titulo());
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var tarefa = tarefaMapper.toEntity(tarefaEnvioDTO);

        tarefa.setCategoria(buscarCategoria(tarefaEnvioDTO.categoriaId()));
        tarefa.setAtividade(buscarAtividade(tarefaEnvioDTO.atividadeId()));
        tarefa.setDataConclusao(tarefa.getSituacao() == Situacao.CONCLUIDA ? agora : null);
        tarefa.setCriadoEm(agora);
        tarefa.setAtualizadoEm(agora);
        tarefaRepository.save(tarefa);

        eventoTarefaService.registrar(tarefa, TipoEventoTarefa.TAREFA_CRIADA, agora);

        if (tarefa.getSituacao() == Situacao.CONCLUIDA) {
            eventoTarefaService.registrar(tarefa, TipoEventoTarefa.TAREFA_CONCLUIDA, agora);
        }

        if (tarefaEnvioDTO.recorrencia() != null) {
            serieRecorrenciaService.iniciar(tarefa, tarefaEnvioDTO.recorrencia(), agora, Set.of());
        }

        return tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora));
    }

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true)
    public TarefaDTO atualizar(Long id, EscopoAlteracao escopo, TarefaEnvioDTO tarefaEnvioDTO) {
        log.info("Atualizando a tarefa... - ID: [{}] - Escopo: {}", id, escopo);
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var tarefa = buscarTarefa(id);
        var dataAnterior = tarefa.getData();
        var prioridadeAnterior = tarefa.getPrioridade();
        var alteracao = new AlteracaoTarefaDTO(
                tarefaEnvioDTO,
                buscarCategoria(tarefaEnvioDTO.categoriaId()),
                buscarAtividade(tarefaEnvioDTO.atividadeId()),
                agora);

        if (tarefa.getSerie() == null) {
            aplicarTudo(tarefa, alteracao);
            iniciarSerie(tarefa, alteracao, Set.of());
        } else {
            atualizarOcorrencia(tarefa, escopo, alteracao);
        }

        registrarMudancas(tarefa, prioridadeAnterior, dataAnterior, agora);

        return tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora));
    }

    @Transactional
    public PaginaDTO<TarefaDTO> listar(FiltroTarefasDTO filtro) {
        log.info("Listando as tarefas... - Filtro: {}", filtro);
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var fuso = fusoHorarioService.obter();

        serieRecorrenciaService.estender(agora);
        var prazos = tarefaRepository.buscarPagina(filtro, fuso, agora);
        var totalItens = tarefaRepository.contar(filtro, fuso, agora);

        var tarefas = tarefaRepository.findComResumosByIdIn(prazos.stream().map(PrazoTarefaDTO::id).toList())
                .stream()
                .collect(Collectors.toMap(Tarefa::getId, Function.identity()));

        var itens = prazos.stream()
                .map(prazo -> tarefaMapper.toDTO(tarefas.get(prazo.id()), prazo.prazo()))
                .toList();

        return new PaginaDTO<>(itens, filtro.pagina(), filtro.tamanho(), totalItens, (int) Math.ceilDiv(totalItens, filtro.tamanho()));
    }

    @Transactional
    public List<DiaCalendarioDTO> resumirCalendario(LocalDate dataInicial, LocalDate dataFinal) {
        log.info("Resumindo o calendário... - De: {} - Até: {}", dataInicial, dataFinal);
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);

        serieRecorrenciaService.estender(agora);

        return tarefaRepository.resumirPorDia(dataInicial, dataFinal, fusoHorarioService.obter(), agora);
    }

    @Transactional
    public TarefaDTO buscar(Long id) {
        log.info("Buscando a tarefa... - ID: [{}]", id);
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);

        serieRecorrenciaService.estender(agora);
        var tarefa = buscarTarefa(id);

        return tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora));
    }

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true)
    public TarefaDTO alterarSituacao(Long id, SituacaoTarefaDTO situacaoTarefaDTO) {
        log.info("Alterando a situação da tarefa... - ID: [{}] - Situação: {}", id, situacaoTarefaDTO.situacao());
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var tarefa = buscarTarefa(id);

        aplicarSituacao(tarefa, situacaoTarefaDTO.situacao(), agora);

        return tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora));
    }

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true)
    public List<TarefaDTO> reagendar(ReagendamentoDTO reagendamentoDTO) {
        log.info("Reagendando as tarefas... - Quantidade: {}", reagendamentoDTO.itens().size());
        var agora = Instant.now(clock).truncatedTo(ChronoUnit.MILLIS);
        var ids = reagendamentoDTO.itens().stream().map(ItemReagendamentoDTO::id).distinct().toList();

        var tarefas = tarefaRepository.findComResumosByIdIn(ids)
                .stream()
                .collect(Collectors.toMap(Tarefa::getId, Function.identity()));

        if (tarefas.size() < ids.size()) {
            throw new TarefaNaoEncontradaException("Uma das tarefas não existe mais!");
        }

        reagendamentoDTO.itens().forEach(item -> mudarData(tarefas.get(item.id()), item.data(), agora));

        return ids.stream()
                .map(tarefas::get)
                .map(tarefa -> tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora)))
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"categorias", "dashboard", "sessoes"}, allEntries = true)
    public void deletar(Long id, EscopoAlteracao escopo) {
        log.info("Excluindo a tarefa... - ID: [{}] - Escopo: {}", id, escopo);
        var tarefa = buscarTarefa(id);
        var removidas = new ArrayList<>(List.of(tarefa));
        var comProximas = escopo == EscopoAlteracao.ESTA_E_PROXIMAS && tarefa.getSerie() != null && tarefa.getData() != null;

        if (comProximas) {
            removidas.addAll(tarefaRepository.findBySerieIdAndDataAfterAndSituacaoIn(tarefa.getSerie().getId(), tarefa.getData(), EM_ABERTO));
        }

        tarefaRepository.deleteAll(removidas);

        if (comProximas) {
            serieRecorrenciaService.encerrarAntesDe(tarefa.getSerie(), tarefa.getData());
        }
    }

    private void mudarData(Tarefa tarefa, LocalDate data, Instant agora) {
        var anterior = tarefa.getData();

        if (!Objects.equals(anterior, data)) {
            tarefa.setData(data);
            tarefa.setAtualizadoEm(agora);
            eventoTarefaService.registrarAlteracao(tarefa, TipoEventoTarefa.DATA_ALTERADA, agora, Objects.toString(anterior, null), data.toString());
        }
    }

    private Tarefa buscarTarefa(Long id) {
        return tarefaRepository.findComResumosById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException("Esta tarefa não existe mais!"));
    }

    private void atualizarOcorrencia(Tarefa tarefa, EscopoAlteracao escopo, AlteracaoTarefaDTO alteracao) {
        var serie = tarefa.getSerie();
        var dataOriginal = tarefa.getData();
        var regraMudou = !Objects.equals(tarefaMapper.toRecorrenciaDTO(serie), alteracao.tarefaEnvioDTO().recorrencia());

        if (escopo == EscopoAlteracao.SOMENTE_ESTA) {
            if (regraMudou) {
                throw new AlteracaoRecorrenciaException("Para mudar a repetição, aplique a alteração a esta e às próximas!");
            }

            aplicarTudo(tarefa, alteracao);
            return;
        }

        var seguintes = tarefaRepository.findBySerieId(serie.getId())
                .stream()
                .filter(ocorrencia -> ocorrencia.getData() != null && ocorrencia.getData().isAfter(dataOriginal))
                .toList();

        if (!regraMudou && Objects.equals(alteracao.tarefaEnvioDTO().data(), dataOriginal)) {
            aplicarConteudo(tarefa, alteracao);
            seguintes.forEach(ocorrencia -> aplicarConteudo(ocorrencia, alteracao));
            aplicarSituacao(tarefa, alteracao.tarefaEnvioDTO().situacao(), alteracao.agora());
            return;
        }

        var emAberto = seguintes.stream()
                .filter(ocorrencia -> EM_ABERTO.contains(ocorrencia.getSituacao()))
                .toList();

        var datasPreservadas = seguintes.stream()
                .filter(ocorrencia -> !EM_ABERTO.contains(ocorrencia.getSituacao()))
                .map(Tarefa::getData)
                .collect(Collectors.toSet());

        tarefaRepository.deleteAll(emAberto);
        tarefa.setSerie(null);
        serieRecorrenciaService.encerrarAntesDe(serie, dataOriginal);

        aplicarTudo(tarefa, alteracao);
        iniciarSerie(tarefa, alteracao, datasPreservadas);
    }

    private void aplicarTudo(Tarefa tarefa, AlteracaoTarefaDTO alteracao) {
        aplicarConteudo(tarefa, alteracao);
        tarefa.setData(alteracao.tarefaEnvioDTO().data());
        aplicarSituacao(tarefa, alteracao.tarefaEnvioDTO().situacao(), alteracao.agora());
    }

    private void aplicarConteudo(Tarefa tarefa, AlteracaoTarefaDTO alteracao) {
        tarefaMapper.updateEntity(alteracao.tarefaEnvioDTO(), tarefa);
        tarefa.setCategoria(alteracao.categoria());
        tarefa.setAtividade(alteracao.atividade());
        tarefa.setAtualizadoEm(alteracao.agora());
    }

    private void aplicarSituacao(Tarefa tarefa, Situacao situacao, Instant agora) {
        var anterior = tarefa.getSituacao();

        if (anterior != situacao) {
            tarefa.setSituacao(situacao);
            tarefa.setDataConclusao(situacao == Situacao.CONCLUIDA ? agora : null);
            tarefa.setAtualizadoEm(agora);
            registrarMudancaDeSituacao(tarefa, anterior, agora);
        }
    }

    private void iniciarSerie(Tarefa tarefa, AlteracaoTarefaDTO alteracao, Set<LocalDate> datasOcupadas) {
        if (alteracao.tarefaEnvioDTO().recorrencia() != null) {
            serieRecorrenciaService.iniciar(tarefa, alteracao.tarefaEnvioDTO().recorrencia(), alteracao.agora(), datasOcupadas);
        }
    }

    private void registrarMudancas(Tarefa tarefa, Prioridade prioridadeAnterior, LocalDate dataAnterior, Instant agora) {
        if (tarefa.getPrioridade() != prioridadeAnterior) {
            eventoTarefaService.registrarAlteracao(tarefa, TipoEventoTarefa.PRIORIDADE_ALTERADA, agora, prioridadeAnterior.name(), tarefa.getPrioridade().name());
        }

        if (!Objects.equals(tarefa.getData(), dataAnterior)) {
            eventoTarefaService.registrarAlteracao(tarefa, TipoEventoTarefa.DATA_ALTERADA, agora, Objects.toString(dataAnterior, null), Objects.toString(tarefa.getData(), null));
        }
    }

    private void registrarMudancaDeSituacao(Tarefa tarefa, Situacao anterior, Instant agora) {
        var encerrada = anterior == Situacao.CONCLUIDA || anterior == Situacao.CANCELADA;

        switch (tarefa.getSituacao()) {
            case CONCLUIDA -> eventoTarefaService.registrar(tarefa, TipoEventoTarefa.TAREFA_CONCLUIDA, agora);
            case CANCELADA -> eventoTarefaService.registrar(tarefa, TipoEventoTarefa.TAREFA_CANCELADA, agora);
            case PENDENTE, EM_ANDAMENTO -> {
                if (encerrada) {
                    eventoTarefaService.registrarAlteracao(tarefa, TipoEventoTarefa.TAREFA_REABERTA, agora, anterior.name(), tarefa.getSituacao().name());
                }
            }
        }
    }

    private Categoria buscarCategoria(Long id) {
        return id == null ? null : categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaIndisponivelException("Esta categoria não existe mais. Escolha outra!"));
    }

    private AtividadeEstudo buscarAtividade(Long id) {
        return id == null ? null : atividadeRepository.findById(id)
                .filter(atividade -> !atividade.isArquivada())
                .orElseThrow(() -> new AtividadeIndisponivelException("Esta atividade não está mais disponível. Escolha outra!"));
    }
}