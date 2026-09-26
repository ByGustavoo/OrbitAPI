package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.model.entity.tarefa.SerieRecorrencia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SerieRecorrenciaRepository extends JpaRepository<SerieRecorrencia, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM SerieRecorrencia s
            WHERE s.geradaAte <= :limiteAntecedencia
            AND (s.dataFim IS NULL OR s.geradaAte < s.dataFim)""")
    List<SerieRecorrencia> buscarParaEstender(LocalDate limiteAntecedencia);
}