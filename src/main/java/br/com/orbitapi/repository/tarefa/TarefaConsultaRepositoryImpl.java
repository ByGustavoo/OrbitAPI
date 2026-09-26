package br.com.orbitapi.repository.tarefa;

import br.com.orbitapi.enums.OrdenacaoTarefas;
import br.com.orbitapi.enums.Prazo;
import br.com.orbitapi.enums.Prioridade;
import br.com.orbitapi.model.dto.dashboard.ContagemTarefasDashboardDTO;
import br.com.orbitapi.model.dto.revisao.ContagemTarefasSemanaDTO;
import br.com.orbitapi.model.dto.tarefa.ConclusoesDiaDTO;
import br.com.orbitapi.model.dto.tarefa.DiaCalendarioDTO;
import br.com.orbitapi.model.dto.tarefa.FiltroTarefasDTO;
import br.com.orbitapi.model.dto.tarefa.PrazoTarefaDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TarefaConsultaRepositoryImpl implements TarefaConsultaRepository {

    private final EntityManager entityManager;
    private static final String PESO_PRIORIDADE = "CASE t.prioridade WHEN 'URGENTE' THEN 4 WHEN 'ALTA' THEN 3 WHEN 'MEDIA' THEN 2 ELSE 1 END";

    @Override
    @SuppressWarnings("unchecked")
    public List<PrazoTarefaDTO> buscarPagina(FiltroTarefasDTO filtro, ZoneId fuso, Instant agora) {
        var parametros = new HashMap<String, Object>();
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + "SELECT t.id, t.prazo FROM tarefas_com_prazo t" + filtrar(filtro, parametros) + ordenar(filtro.ordenacao()) + " LIMIT :tamanho OFFSET :deslocamento";

        parametros.put("tamanho", filtro.tamanho());
        parametros.put("deslocamento", (long) filtro.pagina() * filtro.tamanho());

        List<Object[]> linhas = criar(sql, parametros, fuso, agora).getResultList();

        return linhas.stream()
                .map(linha -> new PrazoTarefaDTO(((Number) linha[0]).longValue(), Prazo.valueOf((String) linha[1])))
                .toList();
    }

    @Override
    public long contar(FiltroTarefasDTO filtro, ZoneId fuso, Instant agora) {
        var parametros = new HashMap<String, Object>();
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + "SELECT COUNT(*) FROM tarefas_com_prazo t" + filtrar(filtro, parametros);

        return ((Number) criar(sql, parametros, fuso, agora).getSingleResult()).longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DiaCalendarioDTO> resumirPorDia(LocalDate dataInicial, LocalDate dataFinal, ZoneId fuso, Instant agora) {
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + """
                SELECT CAST(t.data AS VARCHAR),
                       COUNT(*),
                       (ARRAY_AGG(t.prioridade ORDER BY %s DESC))[1],
                       COUNT(*) FILTER (WHERE t.prazo = 'ATRASADA'),
                       COUNT(*) FILTER (WHERE t.situacao = 'CONCLUIDA')
                FROM tarefas_com_prazo t
                WHERE t.data BETWEEN :dataInicial AND :dataFinal
                AND t.situacao <> 'CANCELADA'
                GROUP BY t.data
                ORDER BY t.data""".formatted(PESO_PRIORIDADE);

        List<Object[]> linhas = criar(sql, Map.<String, Object>of("dataInicial", dataInicial, "dataFinal", dataFinal), fuso, agora).getResultList();

        return linhas.stream()
                .map(linha -> new DiaCalendarioDTO(
                        LocalDate.parse((String) linha[0]),
                        ((Number) linha[1]).longValue(),
                        Prioridade.valueOf((String) linha[2]),
                        ((Number) linha[3]).longValue(),
                        ((Number) linha[4]).longValue()))
                .toList();
    }

    @Override
    public ContagemTarefasSemanaDTO contarSemana(LocalDate inicioSemana, LocalDate hoje, ZoneId fuso, Instant agora) {
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + """
                SELECT COUNT(*) FILTER (WHERE t.prazo = 'ATRASADA'),
                       COUNT(*) FILTER (WHERE t.data <= :hoje AND t.situacao <> 'CANCELADA'),
                       COUNT(*) FILTER (WHERE t.data <= :hoje AND t.situacao = 'CONCLUIDA'),
                       COUNT(*) FILTER (WHERE t.situacao <> 'CANCELADA'),
                       COUNT(*) FILTER (WHERE t.situacao = 'CONCLUIDA'),
                       COUNT(*) FILTER (WHERE t.prazo = 'CONCLUIDA_COM_ATRASO'),
                       COUNT(*) FILTER (WHERE t.situacao IN ('PENDENTE', 'EM_ANDAMENTO') AND t.prazo = 'NO_PRAZO'),
                       COUNT(*) FILTER (WHERE t.prazo = 'NAO_REALIZADA'),
                       COUNT(*) FILTER (WHERE t.situacao = 'CANCELADA')
                FROM tarefas_com_prazo t
                WHERE t.data BETWEEN :inicioSemana AND :fimSemana""";

        var parametros = Map.<String, Object>of("inicioSemana", inicioSemana, "fimSemana", inicioSemana.plusDays(6), "hoje", hoje);
        var linha = (Object[]) criar(sql, parametros, fuso, agora).getSingleResult();

        return new ContagemTarefasSemanaDTO(
                ((Number) linha[0]).longValue(),
                ((Number) linha[1]).longValue(),
                ((Number) linha[2]).longValue(),
                ((Number) linha[3]).longValue(),
                ((Number) linha[4]).longValue(),
                ((Number) linha[5]).longValue(),
                ((Number) linha[6]).longValue(),
                ((Number) linha[7]).longValue(),
                ((Number) linha[8]).longValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PrazoTarefaDTO> buscarEmAberto(LocalDate dataInicial, LocalDate dataFinal, boolean semNaoRealizadas, ZoneId fuso, Instant agora) {
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + """
                SELECT t.id, t.prazo
                FROM tarefas_com_prazo t
                WHERE t.data BETWEEN :dataInicial AND :dataFinal
                AND t.situacao IN ('PENDENTE', 'EM_ANDAMENTO')""" + (semNaoRealizadas ? " AND t.prazo <> 'NAO_REALIZADA'" : "") + ordenar(OrdenacaoTarefas.DATA);

        List<Object[]> linhas = criar(sql, Map.<String, Object>of("dataInicial", dataInicial, "dataFinal", dataFinal), fuso, agora).getResultList();

        return linhas.stream()
                .map(linha -> new PrazoTarefaDTO(((Number) linha[0]).longValue(), Prazo.valueOf((String) linha[1])))
                .toList();
    }

    @Override
    public long contarPorPrazo(Prazo prazo, ZoneId fuso, Instant agora) {
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + "SELECT COUNT(*) FROM tarefas_com_prazo t WHERE t.prazo = :prazo";

        return ((Number) criar(sql, Map.<String, Object>of("prazo", prazo.name()), fuso, agora).getSingleResult()).longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ConclusoesDiaDTO> contarConclusoesPorDia(Instant inicio, Instant fim, ZoneId fuso) {
        var consulta = entityManager.createNativeQuery("""
                SELECT CAST(CAST(t.data_conclusao AT TIME ZONE :fuso AS DATE) AS VARCHAR), COUNT(*)
                FROM orbitapi.tarefas t
                WHERE t.data_conclusao >= :inicio
                AND t.data_conclusao < :fim
                GROUP BY 1
                ORDER BY 1""");

        consulta.setParameter("fuso", fuso.getId());
        consulta.setParameter("inicio", inicio.atOffset(ZoneOffset.UTC));
        consulta.setParameter("fim", fim.atOffset(ZoneOffset.UTC));

        List<Object[]> linhas = consulta.getResultList();

        return linhas.stream()
                .map(linha -> new ConclusoesDiaDTO(LocalDate.parse((String) linha[0]), ((Number) linha[1]).longValue()))
                .toList();
    }

    @Override
    public ContagemTarefasDashboardDTO contarParaDashboard(LocalDate inicioSemana, ZoneId fuso, Instant agora) {
        var sql = TarefaPrazoSql.TAREFAS_COM_PRAZO + """
                SELECT COUNT(*) FILTER (WHERE t.situacao = 'PENDENTE' AND t.prazo = 'NO_PRAZO' AND t.data BETWEEN :inicioSemana AND :fimSemana),
                       COUNT(*) FILTER (WHERE t.situacao = 'EM_ANDAMENTO' AND t.prazo = 'NO_PRAZO' AND t.data BETWEEN :inicioSemana AND :fimSemana),
                       COUNT(*) FILTER (WHERE t.prazo = 'ATRASADA'),
                       COUNT(*) FILTER (WHERE t.situacao IN ('PENDENTE', 'EM_ANDAMENTO') AND t.prioridade = 'BAIXA'),
                       COUNT(*) FILTER (WHERE t.situacao IN ('PENDENTE', 'EM_ANDAMENTO') AND t.prioridade = 'MEDIA'),
                       COUNT(*) FILTER (WHERE t.situacao IN ('PENDENTE', 'EM_ANDAMENTO') AND t.prioridade = 'ALTA'),
                       COUNT(*) FILTER (WHERE t.situacao IN ('PENDENTE', 'EM_ANDAMENTO') AND t.prioridade = 'URGENTE')
                FROM tarefas_com_prazo t""";

        var parametros = Map.<String, Object>of("inicioSemana", inicioSemana, "fimSemana", inicioSemana.plusDays(6));
        var linha = (Object[]) criar(sql, parametros, fuso, agora).getSingleResult();

        return new ContagemTarefasDashboardDTO(
                ((Number) linha[0]).longValue(),
                ((Number) linha[1]).longValue(),
                ((Number) linha[2]).longValue(),
                Map.of(
                        Prioridade.BAIXA, ((Number) linha[3]).longValue(),
                        Prioridade.MEDIA, ((Number) linha[4]).longValue(),
                        Prioridade.ALTA, ((Number) linha[5]).longValue(),
                        Prioridade.URGENTE, ((Number) linha[6]).longValue()));
    }

    private Query criar(String sql, Map<String, Object> parametros, ZoneId fuso, Instant agora) {
        var consulta = entityManager.createNativeQuery(sql);

        consulta.setParameter("fuso", fuso.getId());
        consulta.setParameter("agora", agora.atOffset(ZoneOffset.UTC));
        parametros.forEach(consulta::setParameter);

        return consulta;
    }

    private String filtrar(FiltroTarefasDTO filtro, Map<String, Object> parametros) {
        var condicoes = new ArrayList<String>();

        adicionar(condicoes, parametros, "t.data = :data", "data", filtro.data());
        adicionar(condicoes, parametros, "t.data >= :dataInicial", "dataInicial", filtro.dataInicial());
        adicionar(condicoes, parametros, "t.data <= :dataFinal", "dataFinal", filtro.dataFinal());
        adicionar(condicoes, parametros, "strpos(lower(t.titulo || ' ' || COALESCE(t.descricao, '')), lower(:busca)) > 0", "busca", filtro.busca());
        adicionar(condicoes, parametros, "t.situacao IN (:situacoes)", "situacoes", nomes(filtro.situacoes()));
        adicionar(condicoes, parametros, "t.prioridade IN (:prioridades)", "prioridades", nomes(filtro.prioridades()));
        adicionar(condicoes, parametros, "t.prazo = :prazo", "prazo", filtro.prazo() == null ? null : filtro.prazo().name());
        adicionar(condicoes, parametros, "t.categoria_id = :categoriaId", "categoriaId", filtro.categoriaId());

        if (filtro.semData()) {
            condicoes.add("t.data IS NULL");
        }

        return condicoes.isEmpty() ? "" : " WHERE " + String.join(" AND ", condicoes);
    }

    private void adicionar(List<String> condicoes, Map<String, Object> parametros, String condicao, String nome, Object valor) {
        if (valor != null) {
            condicoes.add(condicao);
            parametros.put(nome, valor);
        }
    }

    private List<String> nomes(List<? extends Enum<?>> valores) {
        return valores.isEmpty() ? null : valores.stream().map(Enum::name).toList();
    }

    private String ordenar(OrdenacaoTarefas ordenacao) {
        var porData = "t.data ASC NULLS LAST, CASE WHEN t.dia_inteiro THEN NULL ELSE t.horario_inicio END ASC NULLS FIRST, " + PESO_PRIORIDADE + " DESC, t.id";

        return " ORDER BY " + switch (ordenacao) {
            case DATA -> porData;
            case PRIORIDADE -> PESO_PRIORIDADE + " DESC, " + porData;
            case ATUALIZACAO -> "t.atualizado_em DESC, t.id DESC";
        };
    }
}