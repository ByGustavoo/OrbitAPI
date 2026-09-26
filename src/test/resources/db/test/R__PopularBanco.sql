TRUNCATE orbitapi.notas_semana, orbitapi.sessoes, orbitapi.eventos_tarefa, orbitapi.tarefas, orbitapi.series_recorrencia, orbitapi.atividades RESTART IDENTITY CASCADE;

INSERT INTO orbitapi.atividades (nome, cor, meta_semanal_minutos, arquivada)
VALUES ('Inglês', 'AZUL', 300, FALSE),
       ('Leitura', 'LARANJA', 240, FALSE),
       ('Matemática', 'VERDE', 120, FALSE),
       ('Violão', 'ROXO', NULL, FALSE),
       ('Francês', 'CINZA', NULL, TRUE);

INSERT INTO orbitapi.tarefas (titulo, data, dia_inteiro, horario_inicio, horario_fim, prioridade, situacao, categoria_id, atividade_id, criado_em, atualizado_em)
VALUES ('Pagar a conta de luz', CURRENT_DATE, TRUE, NULL, NULL, 'ALTA', 'PENDENTE', (SELECT id FROM orbitapi.categorias WHERE nome = 'Casa e família'), NULL, now(), now()),
       ('Revisar verbos irregulares', CURRENT_DATE + 1, FALSE, '19:00', '20:00', 'MEDIA', 'PENDENTE', (SELECT id FROM orbitapi.categorias WHERE nome = 'Estudos'), (SELECT id FROM orbitapi.atividades WHERE nome = 'Inglês'), now(), now()),
       ('Trocar a lâmpada', NULL, FALSE, NULL, NULL, 'BAIXA', 'PENDENTE', NULL, NULL, now(), now());

INSERT INTO orbitapi.series_recorrencia (data_inicial, frequencia, data_fim, gerada_ate)
VALUES (CURRENT_DATE - 3, 'DIARIA', NULL, CURRENT_DATE + 30);

INSERT INTO orbitapi.tarefas (titulo, data, dia_inteiro, prioridade, situacao, serie_id, criado_em, atualizado_em)
SELECT 'Meditação', dia::date, TRUE, 'MEDIA', 'PENDENTE', (SELECT id FROM orbitapi.series_recorrencia), now(), now()
FROM generate_series(CURRENT_DATE - 3, CURRENT_DATE + 30, INTERVAL '1 day') AS dia;

INSERT INTO orbitapi.sessoes (atividade_id, tarefa_id, modo, origem, inicio, fim, duracao_segundos, ciclos_concluidos, observacao)
VALUES ((SELECT id FROM orbitapi.atividades WHERE nome = 'Inglês'), (SELECT id FROM orbitapi.tarefas WHERE titulo = 'Revisar verbos irregulares'), 'POMODORO', 'CRONOMETRO', now() - INTERVAL '2 days 1 hour', now() - INTERVAL '2 days', 3000, 2, 'Revisei a lição 12.'),
       ((SELECT id FROM orbitapi.atividades WHERE nome = 'Leitura'), NULL, 'LIVRE', 'MANUAL', now() - INTERVAL '1 day 2 hours', now() - INTERVAL '1 day 1 hour', 3600, NULL, NULL);

INSERT INTO orbitapi.eventos_tarefa (tipo, tarefa_id, titulo, ocorrido_em, anterior, novo)
VALUES ('TAREFA_CRIADA', (SELECT id FROM orbitapi.tarefas WHERE titulo = 'Pagar a conta de luz'), 'Pagar a conta de luz', now() - INTERVAL '1 day', NULL, NULL),
       ('PRIORIDADE_ALTERADA', (SELECT id FROM orbitapi.tarefas WHERE titulo = 'Pagar a conta de luz'), 'Pagar a conta de luz', now() - INTERVAL '20 hours', 'MEDIA', 'ALTA');

INSERT INTO orbitapi.notas_semana (inicio_semana, texto, atualizado_em)
VALUES (CURRENT_DATE - EXTRACT(DOW FROM CURRENT_DATE)::INTEGER - 7, 'Semana puxada no trabalho, mas mantive o inglês.', now() - INTERVAL '3 days');