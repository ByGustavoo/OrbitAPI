package br.com.orbitapi.repository.tarefa;

public final class TarefaPrazoSql {

    public static final String TAREFAS_COM_PRAZO = """
            WITH limites AS (
                SELECT t.*,
                       CASE
                           WHEN t.data IS NULL THEN NULL
                           WHEN t.dia_inteiro OR t.horario_fim IS NULL THEN CAST(t.data + 1 AS TIMESTAMP) AT TIME ZONE :fuso
                           ELSE (t.data + t.horario_fim) AT TIME ZONE :fuso
                       END AS limite
                FROM orbitapi.tarefas t
            ),
            prazos AS (
                SELECT l.*,
                       CASE
                           WHEN l.situacao = 'CONCLUIDA' AND (l.limite IS NULL OR l.data_conclusao IS NULL OR l.data_conclusao <= l.limite) THEN 'CONCLUIDA_NO_PRAZO'
                           WHEN l.situacao = 'CONCLUIDA' THEN 'CONCLUIDA_COM_ATRASO'
                           WHEN l.limite IS NULL THEN 'SEM_DATA'
                           WHEN l.situacao = 'CANCELADA' THEN 'NO_PRAZO'
                           WHEN CAST(:agora AS TIMESTAMPTZ) >= l.limite THEN 'ATRASADA'
                           ELSE 'NO_PRAZO'
                       END AS prazo_isolado
                FROM limites l
            ),
            tarefas_com_prazo AS (
                SELECT p.*,
                       CASE
                           WHEN p.prazo_isolado = 'ATRASADA'
                                AND p.serie_id IS NOT NULL
                                AND p.data + COALESCE(p.horario_inicio, TIME '00:00') < MAX(CASE WHEN p.prazo_isolado = 'ATRASADA' THEN p.data + COALESCE(p.horario_inicio, TIME '00:00') END) OVER (PARTITION BY p.serie_id)
                           THEN 'NAO_REALIZADA'
                           ELSE p.prazo_isolado
                       END AS prazo
                FROM prazos p
            )
            """;

    private TarefaPrazoSql() {}
}