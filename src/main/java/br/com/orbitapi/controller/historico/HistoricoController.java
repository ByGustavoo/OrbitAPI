package br.com.orbitapi.controller.historico;

import br.com.orbitapi.enums.AreaHistorico;
import br.com.orbitapi.model.dto.historico.DetalheHistoricoDTO;
import br.com.orbitapi.model.dto.historico.FiltroHistoricoDTO;
import br.com.orbitapi.model.dto.historico.RegistroHistoricoDTO;
import br.com.orbitapi.model.dto.pagina.PaginaDTO;
import br.com.orbitapi.service.historico.HistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/historico")
public class HistoricoController implements HistoricoDocs {

    private final HistoricoService historicoService;

    @Override
    public ResponseEntity<PaginaDTO<RegistroHistoricoDTO>> listarHistorico(LocalDate dataInicial, LocalDate dataFinal, AreaHistorico area, String busca, int pagina, int tamanho) {
        return ResponseEntity.ok(historicoService.listar(new FiltroHistoricoDTO(dataInicial, dataFinal, area, busca, pagina, tamanho)));
    }

    @Override
    public ResponseEntity<DetalheHistoricoDTO> buscarRegistroHistorico(String id) {
        return ResponseEntity.ok(historicoService.buscar(id));
    }
}