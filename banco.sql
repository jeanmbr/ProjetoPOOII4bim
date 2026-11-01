CREATE DATABASE IF NOT EXISTS petshop;

USE petshop;

CREATE TABLE IF NOT EXISTS consultas (
    horario_index  INT          NOT NULL PRIMARY KEY,
    especie        VARCHAR(10)  NOT NULL,
    nome_animal    VARCHAR(100) NOT NULL,
    idade          INT          NOT NULL,
    nome_dono      VARCHAR(100) NOT NULL,
    telefone_dono  VARCHAR(11)  NOT NULL,
    caracteristica VARCHAR(20)  NOT NULL,
    sintomas       VARCHAR(255) NOT NULL,
    CHECK (horario_index BETWEEN 0 AND 9),
    CHECK (especie IN ('Cachorro', 'Gato')),
    CHECK (idade BETWEEN 0 AND 38)
);
