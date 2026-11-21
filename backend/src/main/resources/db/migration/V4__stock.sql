-- Warehouses, stock per (article, depot) and the immutable movement journal.
CREATE TABLE depot (
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(20)  NOT NULL UNIQUE,
    libelle    VARCHAR(100) NOT NULL,
    adresse    VARCHAR(255),
    actif      BOOLEAN      NOT NULL DEFAULT TRUE,
    par_defaut BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE stock_article_depot (
    id         BIGSERIAL PRIMARY KEY,
    article_id BIGINT         NOT NULL REFERENCES article (id),
    depot_id   BIGINT         NOT NULL REFERENCES depot (id),
    quantite   NUMERIC(15, 3) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT ux_stock_article_depot UNIQUE (article_id, depot_id)
);
CREATE INDEX ix_stock_depot ON stock_article_depot (depot_id);

CREATE TABLE stock_mouvement (
    id               BIGSERIAL PRIMARY KEY,
    date_mouvement   TIMESTAMPTZ    NOT NULL,
    article_id       BIGINT         NOT NULL REFERENCES article (id),
    depot_id         BIGINT         NOT NULL REFERENCES depot (id),
    type             VARCHAR(30)    NOT NULL,
    quantite         NUMERIC(15, 3) NOT NULL,
    cout_unitaire    NUMERIC(15, 3) NOT NULL DEFAULT 0,
    stock_apres      NUMERIC(15, 3) NOT NULL,
    pump_apres       NUMERIC(15, 3) NOT NULL,
    reference_type   VARCHAR(30),
    reference_id     BIGINT,
    reference_numero VARCHAR(30),
    libelle          VARCHAR(255),
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by       VARCHAR(50),
    updated_by       VARCHAR(50),
    version          BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_mouvement_article ON stock_mouvement (article_id, date_mouvement);
CREATE INDEX ix_mouvement_depot ON stock_mouvement (depot_id);
CREATE INDEX ix_mouvement_reference ON stock_mouvement (reference_type, reference_id);
