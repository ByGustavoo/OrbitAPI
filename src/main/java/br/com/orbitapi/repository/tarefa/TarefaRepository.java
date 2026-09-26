package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.enums.Situacao;
import br.com.orbitapi.model.dto.categoria.QuantidadeTarefasCategoriaDTO;
import br.com.orbitapi.model.entity.tarefa.Tarefa;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long>, TarefaConsultaRepository {

    long countByCategoriaId(Long categoriaId);

    @EntityGraph(attributePaths = {"categoria", "atividade", "serie"})
    Optional<Tarefa> findComResumosById(Long id);

    @EntityGraph(attributePaths = {"categoria", "atividade", "serie"})
    List<Tarefa> findComResumosByIdIn(Collection<Long> ids);

    Optional<Tarefa> findFirstBySerieIdOrderByDataDesc(Long serieId);

    List<Tarefa> findBySerieId(Long serieId);

    boolean existsBySerieIdAndDataBefore(Long serieId, LocalDate data);

    List<Tarefa> findBySerieIdAndDataAfterAndSituacaoIn(Long serieId, LocalDate data, Collection<Situacao> situacoes);

    List<Tarefa> findBySerieIdAndSituacaoInAndDataBetween(Long serieId, Collection<Situacao> situacoes, LocalDate inicio, LocalDate fim);

    @Query("""
            SELECT new br.com.orbitapi.model.dto.categoria.QuantidadeTarefasCategoriaDTO(t.categoria.id, COUNT(t))
            FROM Tarefa t
            WHERE t.categoria IS NOT NULL
            GROUP BY t.categoria.id""")
    List<QuantidadeTarefasCategoriaDTO> contarPorCategoria();
}