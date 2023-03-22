-- liquibase formatted sql

-- changeset jitzerttok51:1
-- Add users table
CREATE TABLE IF NOT EXISTS "users"
(
    "id"            BIGSERIAL           NOT NULL,
    "created_date"  TIMESTAMP(6)        NOT NULL,
    "modified_date" TIMESTAMP(6)        NOT NULL,
    "username"      VARCHAR(255) UNIQUE NOT NULL,
    "email"         VARCHAR(255) UNIQUE NOT NULL,
    "hash"          VARCHAR(255)        NOT NULL,
    PRIMARY KEY ("id")
);

-- changeset jitzerttok51:2
-- Set username and email for user profile to be unique
ALTER TABLE "users" ADD CONSTRAINT UC_User UNIQUE ("username", "email");

-- changeset jitzerttok51:3
-- Add files table
CREATE TABLE IF NOT EXISTS "files"
(
    "id"            BIGSERIAL           NOT NULL,
    "created_date"  TIMESTAMP(6)        NOT NULL,
    "modified_date" TIMESTAMP(6)        NOT NULL,
    "sha256"        VARCHAR(255) UNIQUE NOT NULL,
    "name"          VARCHAR(255) UNIQUE NOT NULL,
    "type"          VARCHAR(255)        NOT NULL,
    "file_type"     VARCHAR(255)        NOT NULL,
    "container"     VARCHAR(255)        NOT NULL,
    "size"          BIGINT              NOT NULL,
    PRIMARY KEY ("id")
);
