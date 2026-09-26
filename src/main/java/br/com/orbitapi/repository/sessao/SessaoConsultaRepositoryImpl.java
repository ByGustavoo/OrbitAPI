package br.com.orbitapi.repository.sessao;

import br.com.orbitapi.enums.Cor;
import br.com.orbitapi.model.dto.atividade.ResumoAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.EstudoPorAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MinutosAtividadeDTO;
import br.com.orbitapi.model.dto.estudo.MinutosDiaDTO;
import br.com.orbitapi.model.dto.estudo.SegundosDiaDTO;
import br.com.orbitapi.model.dto.revisao.EstudoAtividadeSemanaDTO;
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
public class SessaoConsultaRepositoryImpl implements SessaoConsultaRepository {

    private final EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<SegundosDiaDTO> somarPorDia(Instant inicio, Instant fim, Long atividadeId, ZoneId fuso) {
        var parametros = new HashMap<String, Object>(Map.of("fuso", fuso.getId()));
        var sql = """
                SELECT CAST(CAST(s.inicio AT TIME ZONE :fuso AS DATE) AS VARCHAR), SUM(s.duracao_segundos), COUNT(*)
                FROM orbitapi.sessoes s""" + filtrar(inicio, fim, atividadeId, parametros) + " GROUP BY 1 ORDER BY 1";

        List<Object[]> linhas = criar(sql, parametros).getResultList();

        return linhas.stream()
                .map(linha -> new SegundosDiaDTO(LocalDate.parse((String) linha[0]), ((Number) linha[1]).longValue(), ((Number) linha[2]).longValue()))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EstudoPorAtividadeDTO> somarPorAtividade(Instant inicio, Instant fim, Long atividadeId) {
        var parametros = new HashMap<String, Object>();
        var sql = """
                SELECT a.id, a.nome, a.cor, SUM(s.duracao_segundos), COUNT(*), FLOOR(EXTRACT(EPOCH FROM MAX(s.fim)) * 1000)
                FROM orbitapi.sessoes s
                JOIN orbitapi.atividades a ON a.id = s.atividade_id""" + filtrar(inicio, fim, atividadeId, parametros) + " GROUP BY a.id, a.nome, a.cor ORDER BY 4 DESC, a.id";

        List<Object[]> linhas = criar(sql, parametros).getResultList();

        return linhas.stream()
                .map(linha -> new EstudoPorAtividadeDTO(
                        new ResumoAtividadeDTO(((Number) linha[0]).longValue(), (String) linha[1], Cor.valueOf((String) linha[2])),
                        ((Number) linha[3]).longValue(),
                        ((Number) linha[4]).longValue(),
                        Instant.ofEpochMilli(((Number) linha[5]).longValue())))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MinutosAtividadeDTO> somarMinutosPorAtividade(Instant inicio, Instant fim) {
        var parametros = new HashMap<String, Object>();
        var sql = """
                SELECT a.id, a.nome, a.cor, SUM(ROUND(s.duracao_segundos / 60.0))
                FROM orbitapi.sessoes s
                JOIN orbitapi.atividades a ON a.id = s.atividade_id""" + filtrar(inicio, fim, null, parametros) + " GROUP BY a.id, a.nome, a.cor ORDER BY 4 DESC, a.id";

        List<Object[]> linhas = criar(sql, parametros).getResultList();

        return linhas.stream()
                .map(linha -> new MinutosAtividadeDTO(
                        new ResumoAtividadeDTO(((Number) linha[0]).longValue(), (String) linha[1], Cor.valueOf((String) linha[2])),
                        ((Number) linha[3]).longValue()))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MinutosDiaDTO> somarMinutosPorDia(Instant inicio, Instant fim, ZoneId fuso) {
        var parametros = new HashMap<String, Object>(Map.of("fuso", fuso.getId()));
        var sql = """
                SELECT CAST(CAST(s.inicio AT TIME ZONE :fuso AS DATE) AS VARCHAR), SUM(ROUND(s.duracao_segundos / 60.0))
                FROM orbitapi.sessoes s""" + filtrar(inicio, fim, null, parametros) + " GROUP BY 1 ORDER BY 1";

        List<Object[]> linhas = criar(sql, parametros).getResultList();

        return linhas.stream()
                .map(linha -> new MinutosDiaDTO(LocalDate.parse((String) linha[0]), ((Number) linha[1]).longValue()))
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EstudoAtividadeSemanaDTO> somarMinutosESessoesPorAtividade(Instant inicio, Instant fim) {
        var parametros = new HashMap<String, Object>();
        var sql = """
                SELECT a.id, a.nome, a.cor, SUM(ROUND(s.duracao_segundos / 60.0)), COUNT(*)
                FROM orbitapi.sessoes s
                JOIN orbitapi.atividades a ON a.id = s.atividade_id""" + filtrar(inicio, fim, null, parametros) + " GROUP BY a.id, a.nome, a.cor";

        List<Object[]> linhas = criar(sql, parametros).getResultList();

        return linhas.stream()
                .map(linha -> new EstudoAtividadeSemanaDTO(
                        new ResumoAtividadeDTO(((Number) linha[0]).longValue(), (String) linha[1], Cor.valueOf((String) linha[2])),
                        ((Number) linha[3]).longValue(),
                        ((Number) linha[4]).longValue()))
                .toList();
    }

    private Query criar(String sql, Map<String, Object> parametros) {
        var consulta = entityManager.createNativeQuery(sql);

        parametros.forEach(consulta::setParameter);

        return consulta;
    }

    private String filtrar(Instant inicio, Instant fim, Long atividadeId, Map<String, Object> parametros) {
        var condicoes = new ArrayList<String>();

        adicionar(condicoes, parametros, "s.inicio >= :inicio", "inicio", inicio == null ? null : inicio.atOffset(ZoneOffset.UTC));
        adicionar(condicoes, parametros, "s.inicio < :fim", "fim", fim == null ? null : fim.atOffset(ZoneOffset.UTC));
        adicionar(condicoes, parametros, "s.atividade_id = :atividadeId", "atividadeId", atividadeId);

        return condicoes.isEmpty() ? "" : " WHERE " + String.join(" AND ", condicoes);
    }

    private void adicionar(List<String> condicoes, Map<String, Object> parametros, String condicao, String nome, Object valor) {
        if (valor != null) {
            condicoes.add(condicao);
            parametros.put(nome, valor);
        }
    }
}