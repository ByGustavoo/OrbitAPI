package br.com.orbitapi.service.tarefa;

import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.model.entity.tarefa.EventoTarefa;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import br.com.orbitapi.repository.tarefa.EventoTarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventoTarefaService {

    private final EventoTarefaRepository eventoTarefaRepository;

    @Transactional
    public void registrar(Tarefa tarefa, TipoEventoTarefa tipo, Instant ocorridoEm) {
        eventoTarefaRepository.save(criar(tarefa, tipo, ocorridoEm));
    }

    @Transactional
    public void registrarAlteracao(Tarefa tarefa, TipoEventoTarefa tipo, Instant ocorridoEm, String anterior, String novo) {
        var evento = criar(tarefa, tipo, ocorridoEm);

        evento.setAnterior(anterior);
        evento.setNovo(novo);

        eventoTarefaRepository.save(evento);
    }

    private EventoTarefa criar(Tarefa tarefa, TipoEventoTarefa tipo, Instant ocorridoEm) {
        var evento = new EventoTarefa();

        evento.setTipo(tipo);
        evento.setTarefa(tarefa);
        evento.setTitulo(tarefa.getTitulo());
        evento.setOcorridoEm(ocorridoEm);

        return evento;
    }
}