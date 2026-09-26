package br.com.orbitapi.repository.dashboard;

import br.com.orbitapi.enums.TipoEventoRecente;
import br.com.orbitapi.model.dto.dashboard.EventoRecenteDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<EventoRecenteDTO> buscarEventosRecentes(Instant agora, int limite) {
        var consulta = entityManager.createNativeQuery("""
                SELECT r.tipo, r.descricao, FLOOR(EXTRACT(EPOCH FROM r.ocorrido_em) * 1000), r.referencia_id
                FROM (
                    SELECT 'evento' AS prefixo, e.id AS numero, e.tipo AS tipo, e.titulo AS descricao, e.ocorrido_em AS ocorrido_em, e.tarefa_id AS referencia_id
                    FROM orbitapi.eventos_tarefa e
                    WHERE e.tipo IN ('TAREFA_CRIADA', 'TAREFA_CONCLUIDA', 'TAREFA_CANCELADA', 'TAREFA_REABERTA')
                    AND e.ocorrido_em <= CAST(:agora AS TIMESTAMPTZ)
                    UNION ALL
                    SELECT 'sessao', s.id, 'SESSAO_SALVA', a.nome, s.inicio, s.id
                    FROM orbitapi.sessoes s
                    JOIN orbitapi.atividades a ON a.id = s.atividade_id
                ) r
                ORDER BY r.ocorrido_em DESC, r.prefixo DESC, r.numero DESC
                LIMIT :limite""");

        consulta.setParameter("agora", agora.atOffset(ZoneOffset.UTC));
        consulta.setParameter("limite", limite);

        List<Object[]> linhas = consulta.getResultList();

        return linhas.stream()
                .map(linha -> new EventoRecenteDTO(
                        TipoEventoRecente.valueOf((String) linha[0]),
                        (String) linha[1],
                        Instant.ofEpochMilli(((Number) linha[2]).longValue()),
                        ((Number) linha[3]).longValue()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    public List<LocalDate> listarDiasComAtividade(LocalDate ate, ZoneId fuso) {
        var consulta = entityManager.createNativeQuery("""
                SELECT DISTINCT CAST(d.dia AS VARCHAR)
                FROM (
                    SELECT CAST(s.inicio AT TIME ZONE :fuso AS DATE) AS dia
                    FROM orbitapi.sessoes s
                    UNION ALL
                    SELECT CAST(t.data_conclusao AT TIME ZONE :fuso AS DATE)
                    FROM orbitapi.tarefas t
                    WHERE t.situacao = 'CONCLUIDA'
                    AND t.data_conclusao IS NOT NULL
                ) d
                WHERE d.dia <= :ate
                ORDER BY 1""");

        consulta.setParameter("fuso", fuso.getId());
        consulta.setParameter("ate", ate);

        List<String> dias = consulta.getResultList();

        return dias.stream().map(LocalDate::parse).toList();
    }
}