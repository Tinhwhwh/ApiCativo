-- script do banco ApiCativo
--
-- Pode rodar quantas vezes quiser: so cria o que ainda nao existe
-- (IF NOT EXISTS / WHERE NOT EXISTS). Antes, rodar de novo num banco
-- que ja tinha as tabelas dava erro na primeira linha e o pgAdmin
-- desfazia o resto, entao a tabela usuario (e o admin) nunca era criada.
--
-- O proprio sistema roda este script sozinho na primeira vez que abre
-- (ver InstaladorBanco.java). Rodar pelo pgAdmin continua funcionando.
-- Se for rodar na mao, crie o banco antes com: CREATE DATABASE apicativo

CREATE TABLE IF NOT EXISTS localizacao (
    id_localizacao SERIAL PRIMARY KEY,
    nome_setor VARCHAR(100) NOT NULL,
    descricao_localizacao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tecnico (
    id_tecnico SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao_tecnico VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS manejo (
    id_manejo SERIAL PRIMARY KEY,
    tipo_procedimento VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS colmeia (
    id_colmeia SERIAL PRIMARY KEY,
    codigo_identificador VARCHAR(50) NOT NULL UNIQUE,
    data_instalacao DATE,
    status VARCHAR(30),
    tamanho_caixa VARCHAR(30),
    estilo_caixa VARCHAR(30),
    id_localizacao INT NOT NULL REFERENCES localizacao(id_localizacao)
);

-- tabela do N para N entre colmeia e manejo
CREATE TABLE IF NOT EXISTS colmeia_manejo (
    id_colmeia_manejo SERIAL PRIMARY KEY,
    id_colmeia INT NOT NULL REFERENCES colmeia(id_colmeia),
    id_manejo INT NOT NULL REFERENCES manejo(id_manejo),
    data_realizacao DATE NOT NULL,
    observacoes VARCHAR(255),
    id_tecnico INT NOT NULL REFERENCES tecnico(id_tecnico)
);

-- view com o historico completo, com os nomes no lugar dos ids
CREATE OR REPLACE VIEW vw_historico_completo AS
SELECT cm.id_colmeia_manejo, c.id_colmeia, c.codigo_identificador, m.id_manejo, m.tipo_procedimento,
       t.id_tecnico, t.nome AS tecnico, l.nome_setor, cm.data_realizacao, cm.observacoes
FROM colmeia_manejo cm
JOIN colmeia c ON c.id_colmeia = cm.id_colmeia
JOIN manejo m ON m.id_manejo = cm.id_manejo
JOIN tecnico t ON t.id_tecnico = cm.id_tecnico
JOIN localizacao l ON l.id_localizacao = c.id_localizacao;

-- usuarios do sistema (login)
-- a senha nunca fica salva, so o hash SHA-256 dela em hexadecimal
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha_hash CHAR(64) NOT NULL
);

-- usuario padrao: login admin, senha admin
INSERT INTO usuario (nome, login, senha_hash)
SELECT 'Administrador', 'admin', encode(sha256('admin'::bytea), 'hex')
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'admin');

-- alguns dados pra testar: cada tabela so recebe os exemplos se estiver vazia
INSERT INTO localizacao (nome_setor, descricao_localizacao)
SELECT * FROM (VALUES ('Setor A', 'Perto do eucaliptal'), ('Setor B', 'Beira do rio')) AS exemplo
WHERE NOT EXISTS (SELECT 1 FROM localizacao);

INSERT INTO tecnico (nome, descricao_tecnico)
SELECT 'Joao', 'Apicultor'
WHERE NOT EXISTS (SELECT 1 FROM tecnico);

INSERT INTO manejo (tipo_procedimento, descricao)
SELECT * FROM (VALUES ('Revisao', 'Revisao geral da colmeia'), ('Colheita', 'Retirada de melgueiras')) AS exemplo
WHERE NOT EXISTS (SELECT 1 FROM manejo);
