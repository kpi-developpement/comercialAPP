-- Complete company / client / supplier files (fields of the "Fichier société / clients / fournisseurs" screens).

-- Société --------------------------------------------------------------------------------------------------------
ALTER TABLE societe
    ADD COLUMN code                VARCHAR(20),
    ADD COLUMN telephone2          VARCHAR(30),
    ADD COLUMN fax                 VARCHAR(30),
    ADD COLUMN code_postal         VARCHAR(10),
    ADD COLUMN code_commune        VARCHAR(20),
    ADD COLUMN observation         TEXT,
    ADD COLUMN date_creation       DATE,
    ADD COLUMN date_debut_activite DATE,
    ADD COLUMN date_fin_activite   DATE,
    ADD COLUMN type_personne       VARCHAR(10)    NOT NULL DEFAULT 'MORALE' CHECK (type_personne IN ('MORALE', 'PHYSIQUE')),
    ADD COLUMN secteur             VARCHAR(10)    NOT NULL DEFAULT 'PRIVE' CHECK (secteur IN ('PRIVE', 'PUBLIC')),
    ADD COLUMN loyer_annuel        NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN entete              TEXT,
    ADD COLUMN subdivision         VARCHAR(60),
    ADD COLUMN raf_reception       VARCHAR(60),
    ADD COLUMN ville_rc            VARCHAR(80),
    ADD COLUMN cnss2               VARCHAR(30),
    ADD COLUMN taux_cm             NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    ADD COLUMN taux_impot          NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    ADD COLUMN soumis_impot        BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN activite            VARCHAR(150),
    ADD COLUMN agence_bancaire     VARCHAR(80),
    ADD COLUMN swift               VARCHAR(20),
    ADD COLUMN capital             NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN effectif            INTEGER        NOT NULL DEFAULT 0,
    ADD COLUMN pp_nom              VARCHAR(80),
    ADD COLUMN pp_prenom           VARCHAR(80),
    ADD COLUMN pp_cin              VARCHAR(20),
    ADD COLUMN pp_qualite          VARCHAR(80);

UPDATE societe SET code = '000' WHERE code IS NULL;

-- Fields shared by clients and suppliers ---------------------------------------------------------------------------
ALTER TABLE client
    ADD COLUMN code_groupe           VARCHAR(30),
    ADD COLUMN pays                  VARCHAR(60)   NOT NULL DEFAULT 'Maroc',
    ADD COLUMN code_postal           VARCHAR(10),
    ADD COLUMN telephone2            VARCHAR(30),
    ADD COLUMN fax                   VARCHAR(30),
    ADD COLUMN site_web              VARCHAR(120),
    ADD COLUMN devise                VARCHAR(3)    NOT NULL DEFAULT 'MAD',
    ADD COLUMN activite              VARCHAR(150),
    ADD COLUMN cnss                  VARCHAR(30),
    ADD COLUMN categorie             VARCHAR(60),
    ADD COLUMN mode_livraison        VARCHAR(60),
    ADD COLUMN escompte              NUMERIC(5, 2) NOT NULL DEFAULT 0,
    ADD COLUMN date_debut_activite   DATE,
    ADD COLUMN date_fin_activite     DATE,
    ADD COLUMN date_creation_societe DATE,
    -- client only
    ADD COLUMN prospect              BOOLEAN       NOT NULL DEFAULT FALSE,
    ADD COLUMN bloque                BOOLEAN       NOT NULL DEFAULT FALSE,
    ADD COLUMN arbitrage             BOOLEAN       NOT NULL DEFAULT FALSE,
    ADD COLUMN groupe_societes       BOOLEAN       NOT NULL DEFAULT FALSE,
    ADD COLUMN type_client           VARCHAR(20)   NOT NULL DEFAULT 'A_TERME'
        CHECK (type_client IN ('COMPTANT', 'DEPOSITAIRE', 'A_TERME', 'AUTRE')),
    ADD COLUMN risque                VARCHAR(20)   NOT NULL DEFAULT 'NORMAL'
        CHECK (risque IN ('NORMAL', 'LITIGIEUX', 'RISQUE')),
    ADD COLUMN representant          VARCHAR(80),
    ADD COLUMN depot_id              BIGINT REFERENCES depot (id),
    ADD COLUMN cin                   VARCHAR(20),
    ADD COLUMN journal               VARCHAR(30),
    ADD COLUMN compte_comptable      VARCHAR(30),
    ADD COLUMN email_relance         VARCHAR(120),
    ADD COLUMN date_debut_prospect   DATE,
    ADD COLUMN date_fin_societe      DATE;

ALTER TABLE fournisseur
    ADD COLUMN code_groupe            VARCHAR(30),
    ADD COLUMN pays                   VARCHAR(60)    NOT NULL DEFAULT 'Maroc',
    ADD COLUMN code_postal            VARCHAR(10),
    ADD COLUMN telephone2             VARCHAR(30),
    ADD COLUMN fax                    VARCHAR(30),
    ADD COLUMN site_web               VARCHAR(120),
    ADD COLUMN devise                 VARCHAR(3)     NOT NULL DEFAULT 'MAD',
    ADD COLUMN activite               VARCHAR(150),
    ADD COLUMN cnss                   VARCHAR(30),
    ADD COLUMN categorie              VARCHAR(60),
    ADD COLUMN mode_livraison         VARCHAR(60),
    ADD COLUMN escompte               NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    ADD COLUMN date_debut_activite    DATE,
    ADD COLUMN date_fin_activite      DATE,
    ADD COLUMN date_creation_societe  DATE,
    -- supplier only
    ADD COLUMN nationalite            VARCHAR(60),
    ADD COLUMN type_fournisseur       VARCHAR(20)    NOT NULL DEFAULT 'A_TERME'
        CHECK (type_fournisseur IN ('COMPTANT', 'A_TERME')),
    ADD COLUMN forme_juridique        VARCHAR(30),
    ADD COLUMN capital                NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN effectif               INTEGER        NOT NULL DEFAULT 0,
    ADD COLUMN non_resident           BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN commercant             BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN validation_superviseur BOOLEAN        NOT NULL DEFAULT FALSE,
    ADD COLUMN delai_livraison_min    INTEGER        NOT NULL DEFAULT 0,
    ADD COLUMN delai_livraison_max    INTEGER        NOT NULL DEFAULT 0,
    ADD COLUMN condition_paiement     VARCHAR(80),
    ADD COLUMN commentaire_paiement   TEXT,
    ADD COLUMN plafond_decouvert      NUMERIC(15, 2) NOT NULL DEFAULT 0,
    ADD COLUMN plafond_remise         NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    ADD COLUMN imputation_tva         VARCHAR(30),
    ADD COLUMN code_tva               NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    ADD COLUMN code_integration       VARCHAR(30),
    ADD COLUMN attestation_numero     VARCHAR(50),
    ADD COLUMN attestation_date       DATE,
    ADD COLUMN attestation_validite   DATE;

-- Supplier contacts ("Interlocuteurs") ----------------------------------------------------------------------------
CREATE TABLE fournisseur_contact (
    id             BIGSERIAL PRIMARY KEY,
    fournisseur_id BIGINT       NOT NULL REFERENCES fournisseur (id) ON DELETE CASCADE,
    nom            VARCHAR(80)  NOT NULL,
    prenom         VARCHAR(80),
    fonction       VARCHAR(80),
    telephone      VARCHAR(30),
    email          VARCHAR(120),
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by     VARCHAR(50),
    updated_by     VARCHAR(50),
    version        BIGINT       NOT NULL DEFAULT 0
);
CREATE INDEX ix_fournisseur_contact ON fournisseur_contact (fournisseur_id);

-- Suggested products catalogue per client ------------------------------------------------------------------------
CREATE TABLE client_article_suggere (
    id         BIGSERIAL PRIMARY KEY,
    client_id  BIGINT      NOT NULL REFERENCES client (id) ON DELETE CASCADE,
    article_id BIGINT      NOT NULL REFERENCES article (id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT ux_client_article_suggere UNIQUE (client_id, article_id)
);
