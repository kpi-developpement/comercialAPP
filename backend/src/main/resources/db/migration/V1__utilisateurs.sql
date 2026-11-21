-- Users & roles. Every table carries the audit/version columns of BaseEntity.
CREATE TABLE utilisateur (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    nom_complet   VARCHAR(120) NOT NULL,
    email         VARCHAR(120),
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'COMMERCIAL', 'MAGASINIER', 'COMPTABLE')),
    actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    version       BIGINT       NOT NULL DEFAULT 0
);
CREATE UNIQUE INDEX ux_utilisateur_username ON utilisateur (lower(username));
