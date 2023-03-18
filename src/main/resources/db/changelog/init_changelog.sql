-- liquibase formatted sql

-- changeset jitzerttok51:1
-- Create dummy table
CREATE TABLE IF NOT EXISTS "dummy"
(
    "id" BIGSERIAL NOT NULL,
    date VARCHAR(255),
    PRIMARY KEY (id)
);

