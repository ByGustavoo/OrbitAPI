package br.com.orbitapi.controller.sessao;

import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.model.dto.sessao.FiltroSessoesDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEnvioDTO;
import br.com.orbitapi.model.dto.sessao.SessaoEstudoDTO;
import br.com.orbitapi.service.sessao.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sessoes")
public class SessaoController implements SessaoDocs {

    private final SessaoService sessaoService;

    @Override
    public ResponseEntity<PaginaDTO<SessaoEstudoDTO>> listarSessoes(LocalDate dataInicial, LocalDate dataFinal, Long atividadeId, int pagina, int tamanho) {
        return ResponseEntity.ok(sessaoService.listar(new FiltroSessoesDTO(dataInicial, dataFinal, atividadeId, pagina, tamanho)));
    }

    @Override
    public ResponseEntity<SessaoEstudoDTO> salvarSessao(SessaoEnvioDTO sessaoEnvioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoService.salvar(sessaoEnvioDTO));
    }

    @Override
    public ResponseEntity<SessaoEstudoDTO> atualizarSessao(Long id, SessaoEnvioDTO sessaoEnvioDTO) {
        return ResponseEntity.ok(sessaoService.atualizar(id, sessaoEnvioDTO));
    }

    @Override
    public ResponseEntity<Void> deletarSessao(Long id) {
        sessaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}