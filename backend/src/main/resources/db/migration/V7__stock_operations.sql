-- Inventory sessions and inter-warehouse transfers.
CREATE TABLE inventaire (
    id              BIGSERIAL PRIMARY KEY,
    numero          VARCHAR(30) UNIQUE,
    depot_id        BIGINT      NOT NULL REFERENCES depot (id),
    date_inventaire DATE        NOT NULL,
    statut          VARCHAR(20) NOT NULL,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),
    version         BIGINT      NOT NULL DEFAULT 0
);

CREATE TABLE inventaire_ligne (
    id                 BIGSERIAL PRIMARY KEY,
    inventaire_id      BIGINT         NOT NULL REFERENCES inventaire (id) ON DELETE CASCADE,
    article_id         BIGINT         NOT NULL REFERENCES article (id),
    article_code       VARCHAR(30)    NOT NULL,
    designation        VARCHAR(255)   NOT NULL,
    quantite_theorique NUMERIC(15, 3) NOT NULL DEFAULT 0,
    quantite_comptee   NUMERIC(15, 3),
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by         VARCHAR(50),
    updated_by         VARCHAR(50),
    version            BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT ux_inventaire_article UNIQUE (inventaire_id, article_id)
);

CREATE TABLE transfert (
    id                   BIGSERIAL PRIMARY KEY,
    numero               VARCHAR(30) UNIQUE,
    depot_source_id      BIGINT      NOT NULL REFERENCES depot (id),
    depot_destination_id BIGINT      NOT NULL REFERENCES depot (id),
    date_transfert       DATE        NOT NULL,
    statut               VARCHAR(20) NOT NULL,
    notes                TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by           VARCHAR(50),
    updated_by           VARCHAR(50),
    version              BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT ck_transfert_depots CHECK (depot_source_id <> depot_destination_id)
);

CREATE TABLE transfert_ligne (
    id           BIGSERIAL PRIMARY KEY,
    transfert_id BIGINT         NOT NULL REFERENCES transfert (id) ON DELETE CASCADE,
    article_id   BIGINT         NOT NULL REFERENCES article (id),
    article_code VARCHAR(30)    NOT NULL,
    designation  VARCHAR(255)   NOT NULL,
    quantite     NUMERIC(15, 3) NOT NULL CHECK (quantite > 0),
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by   VARCHAR(50),
    updated_by   VARCHAR(50),
    version      BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_transfert_ligne_transfert ON transfert_ligne (transfert_id);
