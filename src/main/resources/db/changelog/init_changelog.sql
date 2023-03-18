-- liquibase formatted sql

-- changeset jitzerttok51:1
-- Add users table
CREATE TABLE IF NOT EXISTS "users"
(
    "id"            BIGSERIAL    NOT NULL,
    "created_date"  TIMESTAMP(6) NOT NULL,
    "modified_date" TIMESTAMP(6) NOT NULL,
    "username"      VARCHAR(255) NOT NULL,
    "email"         VARCHAR(255) NOT NULL,
    "hash"          VARCHAR(255) NOT NULL,
    PRIMARY KEY ("id")
);
