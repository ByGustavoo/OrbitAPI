package br.com.orbitapi.controller.dashboard;

import br.com.orbitapi.model.dto.dashboard.ResumoDashboardDTO;
import br.com.orbitapi.model.dto.dashboard.SequenciaDTO;
import br.com.orbitapi.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dashboard")
public class DashboardController implements DashboardDocs {

    private final DashboardService dashboardService;

    @Override
    public ResponseEntity<ResumoDashboardDTO> buscarResumoDashboard(LocalDate dataInicial, LocalDate dataFinal) {
        return ResponseEntity.ok(dashboardService.resumir(dataInicial, dataFinal));
    }

    @Override
    public ResponseEntity<SequenciaDTO> buscarSequencia(LocalDate data) {
        return ResponseEntity.ok(dashboardService.buscarSequencia(data));
    }
}