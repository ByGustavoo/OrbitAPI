package br.com.orbitapi.controller.estudo;

import br.com.orbitapi.model.dto.estudo.MapaCalorDTO;
import br.com.orbitapi.model.dto.estudo.ProgressoMetaDTO;
import br.com.orbitapi.model.dto.estudo.ResumoEstudosDTO;
import br.com.orbitapi.service.estudo.EstudoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/estudos")
public class EstudoController implements EstudoDocs {

    private final EstudoService estudoService;

    @Override
    public ResponseEntity<ResumoEstudosDTO> buscarResumoEstudos(LocalDate dataInicial, LocalDate dataFinal, Long atividadeId) {
        return ResponseEntity.ok(estudoService.resumir(dataInicial, dataFinal, atividadeId));
    }

    @Override
    public ResponseEntity<List<ProgressoMetaDTO>> buscarProgressoSemanal(LocalDate inicioSemana) {
        return ResponseEntity.ok(estudoService.buscarProgressoSemanal(inicioSemana));
    }

    @Override
    public ResponseEntity<MapaCalorDTO> buscarMapaCalor(LocalDate dataInicial, LocalDate dataFinal) {
        return ResponseEntity.ok(estudoService.gerarMapaCalor(dataInicial, dataFinal));
    }
}