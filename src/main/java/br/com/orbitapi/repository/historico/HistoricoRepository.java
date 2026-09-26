package br.com.orbitapi.repository.historico;

import br.com.orbitapi.enums.AreaHistorico;
import br.com.orbitapi.enums.Cor;
import br.com.orbitapi.enums.TipoEventoHistorico;
import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import br.com.orbitapi.model.dto.categoria.ResumoCategoriaDTO;
import br.com.orbitapi.model.dto.historico.AlteracaoDTO;
import br.com.orbitapi.model.dto.historico.FiltroHistoricoDTO;
import br.com.orbitapi.model.dto.historico.RegistroHistoricoDTO;
import br.com.orbitapi.repository.tarefa.TarefaPrazoSql;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class HistoricoRepository {

    private final EntityManager entityManager;
    private static final Set<TipoEventoHistorico> COM_ALTERACAO = Set.of(TipoEventoHistorico.PRIORIDADE_ALTERADA, TipoEventoHistorico.DATA_ALTERADA, TipoEventoHistorico.TAREFA_REABERTA);
    private static final String REGISTROS = TarefaPrazoSql.TAREFAS_COM_PRAZO + """
            , registros AS (
                SELECT 'evento' AS prefixo, e.id AS numero, e.tipo AS tipo, 'TAREFAS' AS area, e.ocorrido_em AS ocorrido_em, TRUE AS com_horario,
                       e.titulo AS titulo, e.tarefa_id AS tarefa_id, CAST(NULL AS BIGINT) AS sessao_id, t.categoria_id AS categoria_id,
                       CAST(NULL AS BIGINT) AS atividade_id, e.anterior AS anterior, e.novo AS novo, CAST(NULL AS INTEGER) AS duracao_segundos
                FROM orbitapi.eventos_tarefa e
                JOIN orbitapi.tarefas t ON t.id = e.tarefa_id
                WHERE e.ocorrido_em <= CAST(:agora AS TIMESTAMPTZ)
                UNION ALL
                SELECT 'sessao', s.id, 'SESSAO_ESTUDO', 'ESTUDOS', s.inicio, TRUE,
                       a.nome, s.tarefa_id, s.id, NULL,
                       s.atividade_id, NULL, NULL, s.duracao_segundos
                FROM orbitapi.sessoes s
                JOIN orbitapi.atividades a ON a.id = s.atividade_id
                UNION ALL
                SELECT 'nao-realizada', n.id, 'TAREFA_NAO_REALIZADA', 'TAREFAS',
                       (n.data + CASE WHEN n.dia_inteiro OR n.horario_inicio IS NULL THEN TIME '23:59' ELSE n.horario_inicio END) AT TIME ZONE :fuso,
                       NOT n.dia_inteiro AND n.horario_inicio IS NOT NULL,
                       n.titulo, n.id, NULL, n.categoria_id,
                       NULL, NULL, NULL, NULL
                FROM tarefas_com_prazo n
                WHERE n.prazo = 'NAO_REALIZADA'
            )
            """;
    private static final String COLUNAS = """
            SELECT r.prefixo, r.numero, r.tipo, r.area, FLOOR(EXTRACT(EPOCH FROM r.ocorrido_em) * 1000), r.com_horario, r.titulo,
                   r.tarefa_id, r.sessao_id, c.id, c.nome, c.cor, a.id, a.nome, a.cor, r.anterior, r.novo, r.duracao_segundos
            """;
    private static final String JUNCOES = """
             FROM registros r
            LEFT JOIN orbitapi.categorias c ON c.id = r.categoria_id
            LEFT JOIN orbitapi.atividades a ON a.id = r.atividade_id""";

    @SuppressWarnings("unchecked")
    public List<RegistroHistoricoDTO> buscarPagina(FiltroHistoricoDTO filtro, Instant inicio, Instant fim, ZoneId fuso, Instant agora) {
        var parametros = new HashMap<String, Object>();
        var sql = REGISTROS + COLUNAS + JUNCOES + filtrar(filtro, inicio, fim, parametros) + " ORDER BY r.ocorrido_em DESC, r.prefixo DESC, r.numero DESC LIMIT :tamanho OFFSET :deslocamento";

        parametros.put("tamanho", filtro.tamanho());
        parametros.put("deslocamento", (long) filtro.pagina() * filtro.tamanho());

        List<Object[]> linhas = criar(sql, parametros, fuso, agora).getResultList();

        return linhas.stream().map(this::paraRegistro).toList();
    }

    public long contar(FiltroHistoricoDTO filtro, Instant inicio, Instant fim, ZoneId fuso, Instant agora) {
        var parametros = new HashMap<String, Object>();
        var sql = REGISTROS + "SELECT COUNT(*)" + JUNCOES + filtrar(filtro, inicio, fim, parametros);

        return ((Number) criar(sql, parametros, fuso, agora).getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    public Optional<RegistroHistoricoDTO> buscarRegistro(String prefixo, long numero, ZoneId fuso, Instant agora) {
        var parametros = new HashMap<String, Object>(Map.of("prefixo", prefixo, "numero", numero));
        var sql = REGISTROS + COLUNAS + JUNCOES + " WHERE r.prefixo = :prefixo AND r.numero = :numero";

        List<Object[]> linhas = criar(sql, parametros, fuso, agora).getResultList();

        return linhas.stream().findFirst().map(this::paraRegistro);
    }

    private Query criar(String sql, Map<String, Object> parametros, ZoneId fuso, Instant agora) {
        var consulta = entityManager.createNativeQuery(sql);

        consulta.setParameter("fuso", fuso.getId());
        consulta.setParameter("agora", agora.atOffset(ZoneOffset.UTC));
        parametros.forEach(consulta::setParameter);

        return consulta;
    }

    private String filtrar(FiltroHistoricoDTO filtro, Instant inicio, Instant fim, Map<String, Object> parametros) {
        var condicoes = new StringBuilder(" WHERE r.ocorrido_em >= CAST(:inicio AS TIMESTAMPTZ) AND r.ocorrido_em < CAST(:fim AS TIMESTAMPTZ)");

        parametros.put("inicio", inicio.atOffset(ZoneOffset.UTC));
        parametros.put("fim", fim.atOffset(ZoneOffset.UTC));

        if (filtro.area() != null) {
            condicoes.append(" AND r.area = :area");
            parametros.put("area", filtro.area().name());
        }

        if (filtro.busca() != null) {
            condicoes.append(" AND strpos(lower(r.titulo || ' ' || COALESCE(c.nome, '') || ' ' || COALESCE(a.nome, '')), lower(:busca)) > 0");
            parametros.put("busca", filtro.busca());
        }

        return condicoes.toString();
    }

    private RegistroHistoricoDTO paraRegistro(Object[] linha) {
        var tipo = TipoEventoHistorico.valueOf((String) linha[2]);
        var numero = ((Number) linha[1]).longValue();

        return new RegistroHistoricoDTO(
                linha[0] + "-" + numero,
                tipo,
                AreaHistorico.valueOf((String) linha[3]),
                Instant.ofEpochMilli(((Number) linha[4]).longValue()),
                (Boolean) linha[5],
                (String) linha[6],
                linha[7] == null ? null : ((Number) linha[7]).longValue(),
                linha[8] == null ? null : ((Number) linha[8]).longValue(),
                linha[9] == null ? null : new ResumoCategoriaDTO(((Number) linha[9]).longValue(), (String) linha[10], Cor.valueOf((String) linha[11])),
                linha[12] == null ? null : new ResumoAtividadeDTO(((Number) linha[12]).longValue(), (String) linha[13], Cor.valueOf((String) linha[14])),
                COM_ALTERACAO.contains(tipo) ? new AlteracaoDTO((String) linha[15], (String) linha[16]) : null,
                linha[17] == null ? null : ((Number) linha[17]).intValue());
    }
}