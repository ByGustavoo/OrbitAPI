package br.com.orbitapi.repository.sessao;

import br.com.orbitapi.model.entity.sessao.SessaoEstudo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;

public final class SessaoSpecification {

    private SessaoSpecification() {}

    public static Specification<SessaoEstudo> filtrar(Instant inicio, Instant fim, Long atividadeId) {
        return (root, query, builder) -> {
            var predicados = new ArrayList<Predicate>();

            if (inicio != null) {
                predicados.add(builder.greaterThanOrEqualTo(root.get("inicio"), inicio));
            }

            if (fim != null) {
                predicados.add(builder.lessThan(root.get("inicio"), fim));
            }

            if (atividadeId != null) {
                predicados.add(builder.equal(root.get("atividade").get("id"), atividadeId));
            }

            return builder.and(predicados.toArray(Predicate[]::new));
        };
    }
}