package br.com.orbitapi.controller.atividade;

import br.com.orbitapi.model.dto.atividade.ArquivamentoAtividadeDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEnvioDTO;
import br.com.orbitapi.model.dto.atividade.AtividadeEstudoDTO;
import br.com.orbitapi.service.atividade.AtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/atividades")
public class AtividadeController implements AtividadeDocs {

    private final AtividadeService atividadeService;

    @Override
    public ResponseEntity<List<AtividadeEstudoDTO>> listarAtividades() {
        return ResponseEntity.ok(atividadeService.listar());
    }

    @Override
    public ResponseEntity<AtividadeEstudoDTO> salvarAtividade(AtividadeEnvioDTO atividadeEnvioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atividadeService.salvar(atividadeEnvioDTO));
    }

    @Override
    public ResponseEntity<AtividadeEstudoDTO> atualizarAtividade(Long id, AtividadeEnvioDTO atividadeEnvioDTO) {
        return ResponseEntity.ok(atividadeService.atualizar(id, atividadeEnvioDTO));
    }

    @Override
    public ResponseEntity<AtividadeEstudoDTO> alterarArquivamentoAtividade(Long id, ArquivamentoAtividadeDTO arquivamentoAtividadeDTO) {
        return ResponseEntity.ok(atividadeService.alterarArquivamento(id, arquivamentoAtividadeDTO));
    }

    @Override
    public ResponseEntity<Void> deletarAtividade(Long id) {
        atividadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}