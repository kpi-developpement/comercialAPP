-- Customer / supplier payments and their allocation to invoices.
CREATE TABLE reglement (
    id             BIGSERIAL PRIMARY KEY,
    numero         VARCHAR(30)    NOT NULL UNIQUE,
    sens           VARCHAR(20)    NOT NULL CHECK (sens IN ('CLIENT', 'FOURNISSEUR')),
    tiers_id       BIGINT         NOT NULL,
    tiers_nom      VARCHAR(150)   NOT NULL,
    date_reglement DATE           NOT NULL,
    mode           VARCHAR(20)    NOT NULL,
    montant        NUMERIC(15, 2) NOT NULL CHECK (montant > 0),
    montant_affecte NUMERIC(15, 2) NOT NULL DEFAULT 0,
    reference      VARCHAR(50),
    banque         VARCHAR(80),
    date_echeance  DATE,
    statut         VARCHAR(20)    NOT NULL,
    notes          TEXT,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    version        BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_reglement_tiers ON reglement (sens, tiers_id);
CREATE INDEX ix_reglement_echeance ON reglement (date_echeance);

CREATE TABLE reglement_affectation (
    id              BIGSERIAL PRIMARY KEY,
    reglement_id    BIGINT         NOT NULL REFERENCES reglement (id) ON DELETE CASCADE,
    document_id     BIGINT         NOT NULL,
    document_numero VARCHAR(30),
    montant         NUMERIC(15, 2) NOT NULL CHECK (montant > 0),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by      VARCHAR(50),
    updated_by      VARCHAR(50),
    version         BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_affectation_reglement ON reglement_affectation (reglement_id);
CREATE INDEX ix_affectation_document ON reglement_affectation (document_id);
