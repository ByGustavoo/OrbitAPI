CREATE TABLE IF NOT EXISTS orbitapi.notas_semana (
    inicio_semana DATE PRIMARY KEY,
    texto VARCHAR(1000) NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    CONSTRAINT notas_semana_domingo_check CHECK (EXTRACT(DOW FROM inicio_semana) = 0),
    CONSTRAINT notas_semana_texto_check CHECK (char_length(texto) >= 1)
);