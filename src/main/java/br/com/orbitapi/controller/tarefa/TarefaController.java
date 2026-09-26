package br.com.orbitapi.controller.tarefa;

import br.com.orbitapi.enums.EscopoAlteracao;
import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.tarefa.DiaCalendarioDTO;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.model.dto.tarefa.ReagendamentoDTO;
import br.com.orbitapi.model.dto.tarefa.SituacaoTarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaDTO;
import br.com.orbitapi.model.dto.tarefa.TarefaEnvioDTO;
import br.com.orbitapi.service.tarefa.TarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tarefas")
public class TarefaController implements TarefaDocs {

    private final TarefaService tarefaService;

    @Override
    public ResponseEntity<PaginaDTO<TarefaDTO>> listarTarefas(LocalDate data, LocalDate dataInicial, LocalDate dataFinal, boolean semData, List<Situacao> situacao, List<Prioridade> prioridade, Prazo prazo, Long categoriaId, String busca, OrdenacaoTarefas ordenacao, int pagina, int tamanho) {
        var filtro = new FiltroTarefasDTO(data, dataInicial, dataFinal, semData, situacao, prioridade, prazo, categoriaId, busca, ordenacao, pagina, tamanho);

        return ResponseEntity.ok(tarefaService.listar(filtro));
    }

    @Override
    public ResponseEntity<List<DiaCalendarioDTO>> buscarResumoCalendario(LocalDate dataInicial, LocalDate dataFinal) {
        return ResponseEntity.ok(tarefaService.resumirCalendario(dataInicial, dataFinal));
    }

    @Override
    public ResponseEntity<TarefaDTO> buscarTarefa(Long id) {
        return ResponseEntity.ok(tarefaService.buscar(id));
    }

    @Override
    public ResponseEntity<TarefaDTO> salvarTarefa(TarefaEnvioDTO tarefaEnvioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.salvar(tarefaEnvioDTO));
    }

    @Override
    public ResponseEntity<TarefaDTO> atualizarTarefa(Long id, EscopoAlteracao escopo, TarefaEnvioDTO tarefaEnvioDTO) {
        return ResponseEntity.ok(tarefaService.atualizar(id, escopo, tarefaEnvioDTO));
    }

    @Override
    public ResponseEntity<TarefaDTO> alterarSituacaoTarefa(Long id, SituacaoTarefaDTO situacaoTarefaDTO) {
        return ResponseEntity.ok(tarefaService.alterarSituacao(id, situacaoTarefaDTO));
    }

    @Override
    public ResponseEntity<List<TarefaDTO>> reagendarTarefas(ReagendamentoDTO reagendamentoDTO) {
        return ResponseEntity.ok(tarefaService.reagendar(reagendamentoDTO));
    }

    @Override
    public ResponseEntity<Void> deletarTarefa(Long id, EscopoAlteracao escopo) {
        tarefaService.deletar(id, escopo);
        return ResponseEntity.noContent().build();
    }
}