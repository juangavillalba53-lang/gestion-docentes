CREATE DATABASE IF NOT EXISTS gestion_docentes
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gestion_docentes;

DROP TABLE IF EXISTS asignaciones;
DROP TABLE IF EXISTS docentes;
DROP TABLE IF EXISTS asignaturas;

CREATE TABLE docentes (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL,
    especialidad  VARCHAR(80),
    PRIMARY KEY (id),
    CONSTRAINT uk_docentes_email UNIQUE (email)
);

CREATE TABLE asignaturas (
    codigo           VARCHAR(20)  NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    horas_semanales  INT          NOT NULL,
    PRIMARY KEY (codigo),
    CONSTRAINT ck_asignaturas_horas CHECK (horas_semanales > 0)
);

CREATE TABLE asignaciones (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    docente_id         BIGINT      NOT NULL,
    asignatura_codigo  VARCHAR(20) NOT NULL,
    horas              INT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_asignacion UNIQUE (docente_id, asignatura_codigo),
    CONSTRAINT fk_asignacion_docente    FOREIGN KEY (docente_id)
        REFERENCES docentes (id) ON DELETE CASCADE,
    CONSTRAINT fk_asignacion_asignatura FOREIGN KEY (asignatura_codigo)
        REFERENCES asignaturas (codigo) ON DELETE CASCADE,
    CONSTRAINT ck_asignaciones_horas CHECK (horas > 0)
);

INSERT INTO docentes (nombre, email, especialidad) VALUES
    ('Rodri Zarate',  'Rodri@gmail.com',            'Programacion'),
    ('Villalba Juan', 'juangavillalba53@gmail.com', 'Base de datos');

INSERT INTO asignaturas (codigo, nombre, horas_semanales) VALUES
    ('PROG1', 'Programacion 1', 4),
    ('BDD',   'Base de datos',  4);

INSERT INTO asignaciones (docente_id, asignatura_codigo, horas) VALUES
    (1, 'PROG1', 4),
    (2, 'BDD',   4);

UPDATE asignaturas
   SET horas_semanales = 6
 WHERE codigo = 'BDD';

SELECT id, nombre, email, especialidad
  FROM docentes
 ORDER BY nombre;

SELECT * FROM asignaturas;

SELECT a.codigo, a.nombre AS asignatura, d.nombre AS docente, ag.horas
  FROM asignaturas a
  LEFT JOIN asignaciones ag ON ag.asignatura_codigo = a.codigo
  LEFT JOIN docentes d      ON d.id = ag.docente_id
 ORDER BY a.codigo, d.nombre;

SELECT d.nombre, COALESCE(SUM(ag.horas), 0) AS carga_total
  FROM docentes d
  LEFT JOIN asignaciones ag ON ag.docente_id = d.id
 GROUP BY d.id, d.nombre
 ORDER BY carga_total DESC;

SELECT a.codigo,
       a.horas_semanales,
       COALESCE(SUM(ag.horas), 0)                     AS horas_asignadas,
       a.horas_semanales - COALESCE(SUM(ag.horas), 0) AS horas_libres
  FROM asignaturas a
  LEFT JOIN asignaciones ag ON ag.asignatura_codigo = a.codigo
 GROUP BY a.codigo, a.horas_semanales
 ORDER BY a.codigo;
