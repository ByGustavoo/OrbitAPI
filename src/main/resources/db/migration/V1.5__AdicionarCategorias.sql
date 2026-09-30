INSERT INTO orbitapi.categorias (nome, cor)
VALUES ('Finanças', 'AMARELO'),
       ('Compras', 'LARANJA'),
       ('Lazer', 'CIANO'),
       ('Amigos', 'VERMELHO'),
       ('Pessoal', 'CINZA')
ON CONFLICT DO NOTHING;
