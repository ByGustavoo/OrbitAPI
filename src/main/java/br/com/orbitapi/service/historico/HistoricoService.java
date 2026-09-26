package br.com.orbitapi.service.historico;

import br.com.orbitapi.exceptions.PeriodoInvalidoException;
import br.com.orbitapi.exceptions.RegistroHistoricoNaoEncontradoException;
import br.com.orbitapi.model.dto.historico.DetalheHistoricoDTO;
import br.com.orbitapi.model.dto.historico.FiltroHistoricoDTO;
import br.com.orbitapi.model.dto.historico.RegistroHistoricoDTO;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.mapper.sessao.SessaoMapper;
import br.com.orbitapi.model.mapper.tarefa.TarefaMapper;
import br.com.orbitapi.repository.historico.HistoricoRepository;
import br.com.orbitapi.repository.sessao.SessaoRepository;
import br.com.orbitapi.repository.tarefa.TarefaRepository;
import br.com.orbitapi.service.fuso.FusoHorarioService;
import br.com.orbitapi.service.tarefa.PrazoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final Clock clock;
    private final TarefaMapper tarefaMapper;
    private final SessaoMapper sessaoMapper;
    private final PrazoService prazoService;
    private final TarefaRepository tarefaRepository;
    private final SessaoRepository sessaoRepository;
    private final FusoHorarioService fusoHorarioService;
    private final HistoricoRepository historicoRepository;
    private static final Pattern FORMATO_ID = Pattern.compile("(evento|sessao|nao-realizada)-(\\d{1,18})");

    @Transactional(readOnly = true)
    public PaginaDTO<RegistroHistoricoDTO> listar(FiltroHistoricoDTO filtro) {
        log.info("Listando o histórico... - Filtro: {}", filtro);

        if (filtro.dataInicial().isAfter(filtro.dataFinal())) {
            throw new PeriodoInvalidoException("A data inicial precisa ser antes da final!");
        }

        var agora = Instant.now(clock);
        var fuso = fusoHorarioService.obter();
        var inicio = fusoHorarioService.inicioDoDia(filtro.dataInicial());
        var fim = fusoHorarioService.inicioDoDia(filtro.dataFinal().plusDays(1));

        var itens = historicoRepository.buscarPagina(filtro, inicio, fim, fuso, agora);
        var totalItens = historicoRepository.contar(filtro, inicio, fim, fuso, agora);

        return new PaginaDTO<>(itens, filtro.pagina(), filtro.tamanho(), totalItens, (int) Math.ceilDiv(totalItens, filtro.tamanho()));
    }

    @Transactional(readOnly = true)
    public DetalheHistoricoDTO buscar(String id) {
        log.info("Buscando o registro do histórico... - ID: [{}]", id);
        var agora = Instant.now(clock);
        var formato = FORMATO_ID.matcher(id);

        var registro = formato.matches()
                ? historicoRepository.buscarRegistro(formato.group(1), Long.parseLong(formato.group(2)), fusoHorarioService.obter(), agora)
                : Optional.<RegistroHistoricoDTO>empty();

        return registro
                .map(encontrado -> new DetalheHistoricoDTO(encontrado, buscarTarefa(encontrado.tarefaId(), agora), buscarSessao(encontrado.sessaoId())))
                .orElseThrow(() -> new RegistroHistoricoNaoEncontradoException("Este registro não existe mais. A tarefa ou a sessão pode ter sido excluída!"));
    }

    private TarefaDTO buscarTarefa(Long id, Instant agora) {
        return id == null ? null : tarefaRepository.findComResumosById(id)
                .map(tarefa -> tarefaMapper.toDTO(tarefa, prazoService.calcular(tarefa, agora)))
                .orElse(null);
    }

    private SessaoEstudoDTO buscarSessao(Long id) {
        return id == null ? null : sessaoRepository.findById(id)
                .map(sessaoMapper::toDTO)
                .orElse(null);
    }
}