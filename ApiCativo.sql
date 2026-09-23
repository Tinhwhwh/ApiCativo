-- script do banco ApiCativo
-- criar o banco antes: CREATE DATABASE apicativo;

CREATE TABLE localizacao (
    id_localizacao SERIAL PRIMARY KEY,
    nome_setor VARCHAR(100) NOT NULL,
    descricao_localizacao VARCHAR(255)
);

CREATE TABLE tecnico (
    id_tecnico SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao_tecnico VARCHAR(255)
);

CREATE TABLE manejo (
    id_manejo SERIAL PRIMARY KEY,
    tipo_procedimento VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

CREATE TABLE colmeia (
    id_colmeia SERIAL PRIMARY KEY,
    codigo_identificador VARCHAR(50) NOT NULL UNIQUE,
    data_instalacao DATE,
    status VARCHAR(30),
    tamanho_caixa VARCHAR(30),
    estilo_caixa VARCHAR(30),
    id_localizacao INT NOT NULL REFERENCES localizacao(id_localizacao)
);

-- tabela do N para N entre colmeia e manejo
CREATE TABLE colmeia_manejo (
    id_colmeia_manejo SERIAL PRIMARY KEY,
    id_colmeia INT NOT NULL REFERENCES colmeia(id_colmeia),
    id_manejo INT NOT NULL REFERENCES manejo(id_manejo),
    data_realizacao DATE NOT NULL,
    observacoes VARCHAR(255),
    id_tecnico INT NOT NULL REFERENCES tecnico(id_tecnico)
);

-- alguns dados pra testar
INSERT INTO localizacao (nome_setor, descricao_localizacao) VALUES ('Setor A', 'Perto do eucaliptal');
INSERT INTO localizacao (nome_setor, descricao_localizacao) VALUES ('Setor B', 'Beira do rio');
INSERT INTO tecnico (nome, descricao_tecnico) VALUES ('Joao', 'Apicultor');
INSERT INTO manejo (tipo_procedimento, descricao) VALUES ('Revisao', 'Revisao geral da colmeia');
INSERT INTO manejo (tipo_procedimento, descricao) VALUES ('Colheita', 'Retirada de melgueiras');
