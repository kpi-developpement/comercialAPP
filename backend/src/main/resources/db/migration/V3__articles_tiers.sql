-- Master data: families, articles, customers, suppliers (soft delete via "supprime").
CREATE TABLE famille (
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(30)  NOT NULL UNIQUE,
    libelle    VARCHAR(120) NOT NULL,
    parent_id  BIGINT REFERENCES famille (id),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX ix_famille_parent ON famille (parent_id);

CREATE TABLE article (
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(30)    NOT NULL UNIQUE,
    designation    VARCHAR(200)   NOT NULL,
    famille_id     BIGINT REFERENCES famille (id),
    unite          VARCHAR(20)    NOT NULL DEFAULT 'U',
    prix_achat_ht  NUMERIC(15, 3) NOT NULL DEFAULT 0,
    prix_vente_ht  NUMERIC(15, 3) NOT NULL DEFAULT 0,
    taux_tva       NUMERIC(5, 2)  NOT NULL DEFAULT 20,
    code_barres    VARCHAR(50) UNIQUE,
    stock_minimum  NUMERIC(15, 3) NOT NULL DEFAULT 0,
    actif          BOOLEAN        NOT NULL DEFAULT TRUE,
    type           VARCHAR(20)    NOT NULL DEFAULT 'STOCKE' CHECK (type IN ('STOCKE', 'SERVICE')),
    pump           NUMERIC(15, 3) NOT NULL DEFAULT 0,
    supprime       BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    version        BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_article_famille ON article (famille_id);
CREATE INDEX ix_article_designation ON article (lower(designation));

CREATE TABLE client (
    id                    BIGSERIAL PRIMARY KEY,
    code                  VARCHAR(30)    NOT NULL UNIQUE,
    raison_sociale        VARCHAR(150)   NOT NULL,
    ice                   VARCHAR(15),
    identifiant_fiscal    VARCHAR(20),
    rc                    VARCHAR(30),
    patente               VARCHAR(30),
    adresse               VARCHAR(255),
    ville                 VARCHAR(80),
    telephone             VARCHAR(30),
    email                 VARCHAR(120),
    mode_reglement_defaut VARCHAR(20),
    delai_paiement        INTEGER        NOT NULL DEFAULT 0,
    remise_defaut         NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    plafond_credit        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    notes                 TEXT,
    actif                 BOOLEAN        NOT NULL DEFAULT TRUE,
    supprime              BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by            VARCHAR(50),
    updated_by            VARCHAR(50),
    version               BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_client_raison_sociale ON client (lower(raison_sociale));

CREATE TABLE fournisseur (
    id                    BIGSERIAL PRIMARY KEY,
    code                  VARCHAR(30)   NOT NULL UNIQUE,
    raison_sociale        VARCHAR(150)  NOT NULL,
    ice                   VARCHAR(15),
    identifiant_fiscal    VARCHAR(20),
    rc                    VARCHAR(30),
    patente               VARCHAR(30),
    adresse               VARCHAR(255),
    ville                 VARCHAR(80),
    telephone             VARCHAR(30),
    email                 VARCHAR(120),
    mode_reglement_defaut VARCHAR(20),
    delai_paiement        INTEGER       NOT NULL DEFAULT 0,
    remise_defaut         NUMERIC(5, 2) NOT NULL DEFAULT 0,
    notes                 TEXT,
    actif                 BOOLEAN       NOT NULL DEFAULT TRUE,
    supprime              BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by            VARCHAR(50),
    updated_by            VARCHAR(50),
    version               BIGINT        NOT NULL DEFAULT 0
);
CREATE INDEX ix_fournisseur_raison_sociale ON fournisseur (lower(raison_sociale));
