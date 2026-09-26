package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.enums.TipoEventoTarefa;
import br.com.orbitapi.model.entity.tarefa.EventoTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface EventoTarefaRepository extends JpaRepository<EventoTarefa, Long> {

    @Query("SELECT COUNT(e) FROM EventoTarefa e WHERE e.tipo = :tipo AND e.ocorridoEm >= :inicio AND e.ocorridoEm < :fim AND e.ocorridoEm <= :agora")
    long contarNoPeriodo(TipoEventoTarefa tipo, Instant inicio, Instant fim, Instant agora);
}