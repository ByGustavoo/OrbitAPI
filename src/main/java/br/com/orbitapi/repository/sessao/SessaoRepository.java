package br.com.orbitapi.repository.sessao;

import br.com.orbitapi.model.entity.sessao.SessaoEstudo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SessaoRepository extends JpaRepository<SessaoEstudo, Long>, JpaSpecificationExecutor<SessaoEstudo>, SessaoConsultaRepository {

    boolean existsByAtividadeId(Long atividadeId);

    @Override
    @EntityGraph(attributePaths = {"atividade", "tarefa"})
    Page<SessaoEstudo> findAll(Specification<SessaoEstudo> specification, Pageable pageable);

    default Page<SessaoEstudo> buscarPagina(Instant inicio, Instant fim, Long atividadeId, Pageable pageable) {
        return findAll(SessaoSpecification.filtrar(inicio, fim, atividadeId), pageable);
    }
}