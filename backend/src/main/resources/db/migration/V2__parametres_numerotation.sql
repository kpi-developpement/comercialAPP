-- Company settings (single row), VAT rates, payment modes and document numbering.
CREATE TABLE societe (
    id                      BIGSERIAL PRIMARY KEY,
    raison_sociale          VARCHAR(150) NOT NULL,
    forme_juridique         VARCHAR(30),
    ice                     VARCHAR(15),
    identifiant_fiscal      VARCHAR(20),
    rc                      VARCHAR(30),
    patente                 VARCHAR(30),
    cnss                    VARCHAR(30),
    adresse                 VARCHAR(255),
    ville                   VARCHAR(80),
    telephone               VARCHAR(30),
    email                   VARCHAR(120),
    site_web                VARCHAR(120),
    banque                  VARCHAR(80),
    rib                     VARCHAR(30),
    logo                    TEXT,
    devise                  VARCHAR(3)   NOT NULL DEFAULT 'MAD',
    pied_de_page            TEXT,
    autoriser_stock_negatif BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by              VARCHAR(50),
    updated_by              VARCHAR(50),
    version                 BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE taux_tva (
    id         BIGSERIAL PRIMARY KEY,
    taux       NUMERIC(5, 2) NOT NULL UNIQUE CHECK (taux >= 0 AND taux <= 100),
    libelle    VARCHAR(50)   NOT NULL,
    actif      BOOLEAN       NOT NULL DEFAULT TRUE,
    par_defaut BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT        NOT NULL DEFAULT 0
);

CREATE TABLE mode_reglement (
    id         BIGSERIAL PRIMARY KEY,
    code       VARCHAR(20) NOT NULL UNIQUE CHECK (code IN ('ESPECES', 'CHEQUE', 'EFFET', 'VIREMENT', 'CARTE')),
    libelle    VARCHAR(50) NOT NULL,
    actif      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version    BIGINT      NOT NULL DEFAULT 0
);

CREATE TABLE numerotation_config (
    id            BIGSERIAL PRIMARY KEY,
    type_document VARCHAR(30) NOT NULL UNIQUE,
    prefixe       VARCHAR(10) NOT NULL,
    longueur      INTEGER     NOT NULL DEFAULT 5 CHECK (longueur BETWEEN 3 AND 10),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    version       BIGINT      NOT NULL DEFAULT 0
);

-- One counter per (document type, year). Rows are locked FOR UPDATE when a number is issued.
CREATE TABLE sequence_document (
    id            BIGSERIAL PRIMARY KEY,
    type_document VARCHAR(30) NOT NULL,
    annee         INTEGER     NOT NULL,
    compteur      BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by    VARCHAR(50),
    updated_by    VARCHAR(50),
    version       BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT ux_sequence_type_annee UNIQUE (type_document, annee)
);

-- Reference data required in every environment
INSERT INTO societe (raison_sociale, devise, autoriser_stock_negatif, pied_de_page)
VALUES ('Ma Société SARL', 'MAD', FALSE, 'Merci pour votre confiance.');

INSERT INTO taux_tva (taux, libelle, actif, par_defaut) VALUES
    (20.00, 'TVA 20 %', TRUE, TRUE),
    (14.00, 'TVA 14 %', TRUE, FALSE),
    (10.00, 'TVA 10 %', TRUE, FALSE),
    (7.00,  'TVA 7 %',  TRUE, FALSE),
    (0.00,  'Exonéré',  TRUE, FALSE);

INSERT INTO mode_reglement (code, libelle, actif) VALUES
    ('ESPECES', 'Espèces', TRUE),
    ('CHEQUE', 'Chèque', TRUE),
    ('EFFET', 'Effet (LCN)', TRUE),
    ('VIREMENT', 'Virement', TRUE),
    ('CARTE', 'Carte bancaire', TRUE);

INSERT INTO numerotation_config (type_document, prefixe, longueur) VALUES
    ('DEVIS', 'DEV', 5),
    ('BON_COMMANDE', 'BC', 5),
    ('BON_LIVRAISON', 'BL', 5),
    ('FACTURE', 'FA', 5),
    ('AVOIR', 'AV', 5),
    ('BC_FOURNISSEUR', 'BCF', 5),
    ('RECEPTION', 'REC', 5),
    ('FACTURE_FOURNISSEUR', 'FAF', 5),
    ('AVOIR_FOURNISSEUR', 'AVF', 5),
    ('REGLEMENT_CLIENT', 'RGC', 5),
    ('REGLEMENT_FOURNISSEUR', 'RGF', 5),
    ('INVENTAIRE', 'INV', 5),
    ('TRANSFERT', 'TRF', 5);
