package br.com.orbitapi.controller.revisao;

import br.com.orbitapi.model.dto.revisao.NotaSemanaDTO;
import br.com.orbitapi.model.dto.revisao.NotaSemanaEnvioDTO;
import br.com.orbitapi.model.dto.revisao.RevisaoSemanalDTO;
import br.com.orbitapi.service.revisao.RevisaoSemanalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/revisao-semanal")
public class RevisaoSemanalController implements RevisaoSemanalDocs {

    private final RevisaoSemanalService revisaoSemanalService;

    @Override
    public ResponseEntity<RevisaoSemanalDTO> buscarRevisaoSemanal(LocalDate inicioSemana) {
        return ResponseEntity.ok(revisaoSemanalService.buscar(inicioSemana));
    }

    @Override
    public ResponseEntity<NotaSemanaDTO> salvarNotaSemana(LocalDate inicioSemana, NotaSemanaEnvioDTO notaSemanaEnvioDTO) {
        return revisaoSemanalService.salvarNota(inicioSemana, notaSemanaEnvioDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}