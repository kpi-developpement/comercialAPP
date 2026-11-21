-- Commercial documents. Sales and purchases share the same header/line structure
-- (see AbstractDocument / AbstractDocumentLigne); "type" tells which document of the chain it is.
CREATE TABLE document_vente (
    id                 BIGSERIAL PRIMARY KEY,
    type               VARCHAR(30)    NOT NULL CHECK (type IN ('DEVIS', 'BON_COMMANDE', 'BON_LIVRAISON', 'FACTURE', 'AVOIR')),
    numero             VARCHAR(30) UNIQUE,
    date_document      DATE           NOT NULL,
    statut             VARCHAR(20)    NOT NULL,
    tiers_id           BIGINT         NOT NULL REFERENCES client (id),
    tiers_code         VARCHAR(30)    NOT NULL,
    tiers_nom          VARCHAR(150)   NOT NULL,
    tiers_ice          VARCHAR(15),
    tiers_adresse      VARCHAR(255),
    depot_id           BIGINT         NOT NULL REFERENCES depot (id),
    notes              TEXT,
    total_ht           NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_remise       NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_tva          NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_ttc          NUMERIC(15, 2) NOT NULL DEFAULT 0,
    date_echeance      DATE,
    montant_regle      NUMERIC(15, 2) NOT NULL DEFAULT 0,
    statut_paiement    VARCHAR(20),
    retour_marchandise BOOLEAN        NOT NULL DEFAULT FALSE,
    sources            VARCHAR(500),
    date_validite      DATE,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by         VARCHAR(50),
    updated_by         VARCHAR(50),
    version            BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_document_vente_type_date ON document_vente (type, date_document);
CREATE INDEX ix_document_vente_tiers ON document_vente (tiers_id);
CREATE INDEX ix_document_vente_statut ON document_vente (statut);

CREATE TABLE document_vente_ligne (
    id                 BIGSERIAL PRIMARY KEY,
    document_id        BIGINT         NOT NULL REFERENCES document_vente (id) ON DELETE CASCADE,
    ordre              INTEGER        NOT NULL,
    article_id         BIGINT REFERENCES article (id),
    article_code       VARCHAR(30),
    designation        VARCHAR(255)   NOT NULL,
    unite              VARCHAR(20),
    quantite           NUMERIC(15, 3) NOT NULL,
    prix_unitaire_ht   NUMERIC(15, 3) NOT NULL,
    remise_pct         NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    taux_tva           NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    montant_remise     NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ht         NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_tva        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ttc        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    quantite_restante  NUMERIC(15, 3) NOT NULL DEFAULT 0,
    source_ligne_id    BIGINT REFERENCES document_vente_ligne (id),
    source_document_id BIGINT REFERENCES document_vente (id),
    article_stocke     BOOLEAN        NOT NULL DEFAULT FALSE,
    impacte_stock      BOOLEAN        NOT NULL DEFAULT FALSE,
    cout_unitaire      NUMERIC(15, 3) NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by         VARCHAR(50),
    updated_by         VARCHAR(50),
    version            BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_document_vente_ligne_doc ON document_vente_ligne (document_id);
CREATE INDEX ix_document_vente_ligne_source ON document_vente_ligne (source_ligne_id);
CREATE INDEX ix_document_vente_ligne_article ON document_vente_ligne (article_id);

CREATE TABLE document_achat (
    id                    BIGSERIAL PRIMARY KEY,
    type                  VARCHAR(30)    NOT NULL CHECK (type IN ('BC_FOURNISSEUR', 'RECEPTION', 'FACTURE_FOURNISSEUR', 'AVOIR_FOURNISSEUR')),
    numero                VARCHAR(30) UNIQUE,
    date_document         DATE           NOT NULL,
    statut                VARCHAR(20)    NOT NULL,
    tiers_id              BIGINT         NOT NULL REFERENCES fournisseur (id),
    tiers_code            VARCHAR(30)    NOT NULL,
    tiers_nom             VARCHAR(150)   NOT NULL,
    tiers_ice             VARCHAR(15),
    tiers_adresse         VARCHAR(255),
    depot_id              BIGINT         NOT NULL REFERENCES depot (id),
    notes                 TEXT,
    total_ht              NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_remise          NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_tva             NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total_ttc             NUMERIC(15, 2) NOT NULL DEFAULT 0,
    date_echeance         DATE,
    montant_regle         NUMERIC(15, 2) NOT NULL DEFAULT 0,
    statut_paiement       VARCHAR(20),
    retour_marchandise    BOOLEAN        NOT NULL DEFAULT FALSE,
    sources               VARCHAR(500),
    reference_fournisseur VARCHAR(50),
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by            VARCHAR(50),
    updated_by            VARCHAR(50),
    version               BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_document_achat_type_date ON document_achat (type, date_document);
CREATE INDEX ix_document_achat_tiers ON document_achat (tiers_id);

CREATE TABLE document_achat_ligne (
    id                 BIGSERIAL PRIMARY KEY,
    document_id        BIGINT         NOT NULL REFERENCES document_achat (id) ON DELETE CASCADE,
    ordre              INTEGER        NOT NULL,
    article_id         BIGINT REFERENCES article (id),
    article_code       VARCHAR(30),
    designation        VARCHAR(255)   NOT NULL,
    unite              VARCHAR(20),
    quantite           NUMERIC(15, 3) NOT NULL,
    prix_unitaire_ht   NUMERIC(15, 3) NOT NULL,
    remise_pct         NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    taux_tva           NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    montant_remise     NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ht         NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_tva        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ttc        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    quantite_restante  NUMERIC(15, 3) NOT NULL DEFAULT 0,
    source_ligne_id    BIGINT REFERENCES document_achat_ligne (id),
    source_document_id BIGINT REFERENCES document_achat (id),
    article_stocke     BOOLEAN        NOT NULL DEFAULT FALSE,
    impacte_stock      BOOLEAN        NOT NULL DEFAULT FALSE,
    cout_unitaire      NUMERIC(15, 3) NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by         VARCHAR(50),
    updated_by         VARCHAR(50),
    version            BIGINT         NOT NULL DEFAULT 0
);
CREATE INDEX ix_document_achat_ligne_doc ON document_achat_ligne (document_id);
CREATE INDEX ix_document_achat_ligne_source ON document_achat_ligne (source_ligne_id);
