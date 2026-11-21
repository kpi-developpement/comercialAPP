-- DEV ONLY (loaded by the "dev" profile through classpath:db/seed).
-- Master data. Documents, stock and payments are generated through the business services by DevDataSeeder
-- so that stock movements, PUMP and numbering stay consistent.

-- Users (passwords: admin -> admin123, others -> demo1234)
INSERT INTO utilisateur (username, password_hash, nom_complet, email, role, actif) VALUES
    ('admin', '$2a$10$0XuIXSob4tmv8g0XKGsFxuF0lfAxm6xxNVY7GPnDgBlPEwywTIwN.', 'Administrateur', 'admin@atlas-distribution.ma', 'ADMIN', TRUE),
    ('commercial', '$2a$10$BmfmelrTN1ONfFKKJwMSc.LqDZlYICANTGnQ6UprYCAKDLtAQ0gh2', 'Yassine Commercial', 'commercial@atlas-distribution.ma', 'COMMERCIAL', TRUE),
    ('magasinier', '$2a$10$BmfmelrTN1ONfFKKJwMSc.LqDZlYICANTGnQ6UprYCAKDLtAQ0gh2', 'Karim Magasinier', 'stock@atlas-distribution.ma', 'MAGASINIER', TRUE),
    ('comptable', '$2a$10$BmfmelrTN1ONfFKKJwMSc.LqDZlYICANTGnQ6UprYCAKDLtAQ0gh2', 'Salma Comptable', 'compta@atlas-distribution.ma', 'COMPTABLE', TRUE);

UPDATE societe SET
    raison_sociale = 'Atlas Distribution SARL', forme_juridique = 'SARL', ice = '001234567000089',
    identifiant_fiscal = '40123456', rc = '345678 Casablanca', patente = '35712345', cnss = '8765432',
    adresse = '120, boulevard Zerktouni', ville = 'Casablanca', telephone = '+212 522 45 67 89',
    email = 'contact@atlas-distribution.ma', site_web = 'www.atlas-distribution.ma', banque = 'Banque Populaire',
    rib = '190780211000123456789012', devise = 'MAD', autoriser_stock_negatif = FALSE,
    pied_de_page = 'Atlas Distribution SARL au capital de 500 000 MAD – Merci pour votre confiance.'
WHERE id = (SELECT min(id) FROM societe);

INSERT INTO depot (code, libelle, adresse, actif, par_defaut) VALUES
    ('CASA', 'Dépôt Casablanca', 'Zone industrielle Ain Sebaa, Casablanca', TRUE, TRUE),
    ('RABAT', 'Dépôt Rabat', 'Quartier industriel Takaddoum, Rabat', TRUE, FALSE),
    ('TANGER', 'Dépôt Tanger', 'Zone franche Gzenaya, Tanger', TRUE, FALSE);

INSERT INTO famille (code, libelle, parent_id) VALUES
    ('INFO', 'Informatique', NULL),
    ('BUR', 'Fournitures de bureau', NULL),
    ('MOB', 'Mobilier', NULL),
    ('SERV', 'Services', NULL);
INSERT INTO famille (code, libelle, parent_id) VALUES
    ('INFO-PC', 'Ordinateurs', (SELECT id FROM famille WHERE code = 'INFO')),
    ('INFO-PER', 'Périphériques', (SELECT id FROM famille WHERE code = 'INFO')),
    ('INFO-RES', 'Réseau', (SELECT id FROM famille WHERE code = 'INFO')),
    ('BUR-PAP', 'Papeterie', (SELECT id FROM famille WHERE code = 'BUR')),
    ('BUR-ECR', 'Écriture', (SELECT id FROM famille WHERE code = 'BUR')),
    ('MOB-SIE', 'Sièges', (SELECT id FROM famille WHERE code = 'MOB')),
    ('MOB-BUR', 'Bureaux et rangements', (SELECT id FROM famille WHERE code = 'MOB'));

INSERT INTO article (code, designation, famille_id, unite, prix_achat_ht, prix_vente_ht, taux_tva, code_barres, stock_minimum, type, pump) VALUES
    ('PC-001', 'Ordinateur portable 14" Core i5 16 Go', (SELECT id FROM famille WHERE code = 'INFO-PC'), 'U', 5200, 6900, 20, '6111000000011', 5, 'STOCKE', 5200),
    ('PC-002', 'Ordinateur portable 15" Core i7 32 Go', (SELECT id FROM famille WHERE code = 'INFO-PC'), 'U', 8400, 10900, 20, '6111000000028', 3, 'STOCKE', 8400),
    ('PC-003', 'Unité centrale bureautique Core i3', (SELECT id FROM famille WHERE code = 'INFO-PC'), 'U', 3600, 4790, 20, '6111000000035', 4, 'STOCKE', 3600),
    ('PC-004', 'Écran 24" Full HD', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 980, 1390, 20, '6111000000042', 10, 'STOCKE', 980),
    ('PC-005', 'Écran 27" QHD', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 1850, 2490, 20, '6111000000059', 5, 'STOCKE', 1850),
    ('PER-001', 'Clavier + souris sans fil', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 160, 249, 20, '6111000000066', 20, 'STOCKE', 160),
    ('PER-002', 'Imprimante laser monochrome', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 1450, 1990, 20, '6111000000073', 4, 'STOCKE', 1450),
    ('PER-003', 'Imprimante multifonction couleur', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 2600, 3490, 20, '6111000000080', 3, 'STOCKE', 2600),
    ('PER-004', 'Toner laser noir', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 420, 650, 20, '6111000000097', 15, 'STOCKE', 420),
    ('PER-005', 'Disque SSD externe 1 To', (SELECT id FROM famille WHERE code = 'INFO-PER'), 'U', 640, 899, 20, '6111000000103', 8, 'STOCKE', 640),
    ('RES-001', 'Switch 24 ports Gigabit', (SELECT id FROM famille WHERE code = 'INFO-RES'), 'U', 1300, 1850, 20, '6111000000110', 3, 'STOCKE', 1300),
    ('RES-002', 'Routeur Wi-Fi 6', (SELECT id FROM famille WHERE code = 'INFO-RES'), 'U', 890, 1290, 20, '6111000000127', 4, 'STOCKE', 890),
    ('RES-003', 'Câble réseau Cat6 (rouleau 305 m)', (SELECT id FROM famille WHERE code = 'INFO-RES'), 'RL', 780, 1150, 20, '6111000000134', 2, 'STOCKE', 780),
    ('PAP-001', 'Ramette papier A4 80 g', (SELECT id FROM famille WHERE code = 'BUR-PAP'), 'U', 32, 45, 20, '6111000000141', 100, 'STOCKE', 32),
    ('PAP-002', 'Ramette papier A3 80 g', (SELECT id FROM famille WHERE code = 'BUR-PAP'), 'U', 68, 95, 20, '6111000000158', 30, 'STOCKE', 68),
    ('PAP-003', 'Classeur à levier dos 8 cm', (SELECT id FROM famille WHERE code = 'BUR-PAP'), 'U', 14, 22, 20, '6111000000165', 50, 'STOCKE', 14),
    ('PAP-004', 'Cahier 200 pages', (SELECT id FROM famille WHERE code = 'BUR-PAP'), 'U', 9, 15, 7, '6111000000172', 80, 'STOCKE', 9),
    ('PAP-005', 'Enveloppes kraft A4 (boîte de 250)', (SELECT id FROM famille WHERE code = 'BUR-PAP'), 'BT', 85, 129, 20, '6111000000189', 10, 'STOCKE', 85),
    ('ECR-001', 'Stylos bille bleus (boîte de 50)', (SELECT id FROM famille WHERE code = 'BUR-ECR'), 'BT', 45, 69, 20, '6111000000196', 20, 'STOCKE', 45),
    ('ECR-002', 'Surligneurs assortis (lot de 4)', (SELECT id FROM famille WHERE code = 'BUR-ECR'), 'LOT', 18, 29, 20, '6111000000202', 30, 'STOCKE', 18),
    ('ECR-003', 'Marqueurs tableau blanc (lot de 4)', (SELECT id FROM famille WHERE code = 'BUR-ECR'), 'LOT', 24, 39, 20, '6111000000219', 25, 'STOCKE', 24),
    ('MOB-001', 'Fauteuil de bureau ergonomique', (SELECT id FROM famille WHERE code = 'MOB-SIE'), 'U', 1150, 1690, 20, '6111000000226', 5, 'STOCKE', 1150),
    ('MOB-002', 'Chaise visiteur', (SELECT id FROM famille WHERE code = 'MOB-SIE'), 'U', 380, 590, 20, '6111000000233', 10, 'STOCKE', 380),
    ('MOB-003', 'Bureau droit 160 x 80', (SELECT id FROM famille WHERE code = 'MOB-BUR'), 'U', 1600, 2350, 20, '6111000000240', 3, 'STOCKE', 1600),
    ('MOB-004', 'Armoire métallique 2 portes', (SELECT id FROM famille WHERE code = 'MOB-BUR'), 'U', 2100, 2990, 20, '6111000000257', 2, 'STOCKE', 2100),
    ('MOB-005', 'Caisson 3 tiroirs', (SELECT id FROM famille WHERE code = 'MOB-BUR'), 'U', 690, 990, 20, '6111000000264', 4, 'STOCKE', 690),
    ('SRV-001', 'Installation et configuration poste', (SELECT id FROM famille WHERE code = 'SERV'), 'H', 0, 350, 20, NULL, 0, 'SERVICE', 0),
    ('SRV-002', 'Contrat de maintenance annuel', (SELECT id FROM famille WHERE code = 'SERV'), 'AN', 0, 4800, 20, NULL, 0, 'SERVICE', 0),
    ('SRV-003', 'Livraison et montage', (SELECT id FROM famille WHERE code = 'SERV'), 'FORF', 0, 250, 20, NULL, 0, 'SERVICE', 0),
    ('SRV-004', 'Formation bureautique (journée)', (SELECT id FROM famille WHERE code = 'SERV'), 'J', 0, 2500, 10, NULL, 0, 'SERVICE', 0);

INSERT INTO client (code, raison_sociale, ice, identifiant_fiscal, rc, adresse, ville, telephone, email, mode_reglement_defaut, delai_paiement, remise_defaut, plafond_credit) VALUES
    ('CL-001', 'Sofitec SARL', '002345678000012', '41234567', '112233', '15, rue Ibn Battouta', 'Casablanca', '+212 522 11 22 33', 'achats@sofitec.ma', 'VIREMENT', 30, 0, 150000),
    ('CL-002', 'Maroc Logistique SA', '002456789000023', '41345678', '223344', 'Lot 45, zone logistique Zenata', 'Mohammedia', '+212 523 22 33 44', 'compta@marocLog.ma', 'CHEQUE', 60, 5, 300000),
    ('CL-003', 'Cabinet Benali & Associés', '002567890000034', '41456789', '334455', '8, avenue Mohammed V', 'Rabat', '+212 537 33 44 55', 'contact@benali-avocats.ma', 'VIREMENT', 30, 0, 50000),
    ('CL-004', 'Clinique Al Amal', '002678901000045', '41567890', '445566', '22, boulevard Abdelmoumen', 'Casablanca', '+212 522 44 55 66', 'admin@alamal.ma', 'EFFET', 90, 3, 200000),
    ('CL-005', 'École Les Orangers', '002789012000056', '41678901', '556677', 'Route d''Agadir', 'Marrakech', '+212 524 55 66 77', 'direction@lesorangers.ma', 'CHEQUE', 30, 2, 80000),
    ('CL-006', 'Tanger Auto Pièces', '002890123000067', '41789012', '667788', 'Zone industrielle Mghogha', 'Tanger', '+212 539 66 77 88', 'contact@tap.ma', 'ESPECES', 0, 0, 20000),
    ('CL-007', 'Hôtel Atlas Agadir', '002901234000078', '41890123', '778899', 'Boulevard du 20 Août', 'Agadir', '+212 528 77 88 99', 'eco@hotelatlas.ma', 'VIREMENT', 45, 0, 120000),
    ('CL-008', 'Fès Textile', '003012345000089', '41901234', '889900', 'Quartier industriel Sidi Brahim', 'Fès', '+212 535 88 99 00', 'achat@fes-textile.ma', 'EFFET', 60, 0, 100000),
    ('CL-009', 'Agence Digitale Nova', '003123456000090', '42012345', '990011', '3, rue Al Moutanabi', 'Rabat', '+212 537 99 00 11', 'hello@nova.ma', 'CARTE', 0, 0, 0),
    ('CL-010', 'Commune Urbaine de Kénitra', '003234567000001', '42123456', NULL, 'Avenue Mohammed V', 'Kénitra', '+212 537 10 20 30', 'marches@kenitra.ma', 'VIREMENT', 90, 0, 0);

INSERT INTO fournisseur (code, raison_sociale, ice, identifiant_fiscal, rc, adresse, ville, telephone, email, mode_reglement_defaut, delai_paiement, remise_defaut) VALUES
    ('FR-001', 'Distri Info Maroc', '004345678000011', '43234567', '123123', 'Technopark, route de Nouaceur', 'Casablanca', '+212 522 30 40 50', 'ventes@distri-info.ma', 'VIREMENT', 60, 0),
    ('FR-002', 'Papeterie Nationale', '004456789000022', '43345678', '234234', '45, rue de Strasbourg', 'Casablanca', '+212 522 31 41 51', 'commandes@papnat.ma', 'CHEQUE', 30, 2),
    ('FR-003', 'Mobilier Pro Maroc', '004567890000033', '43456789', '345345', 'Zone industrielle Ain Atiq', 'Témara', '+212 537 32 42 52', 'pro@mobilier.ma', 'EFFET', 60, 0),
    ('FR-004', 'Net Solutions Import', '004678901000044', '43567890', '456456', 'Zone franche TFZ', 'Tanger', '+212 539 33 43 53', 'import@netsol.ma', 'VIREMENT', 45, 0),
    ('FR-005', 'Bureau Express', '004789012000055', '43678901', '567567', '12, avenue Hassan II', 'Rabat', '+212 537 34 44 54', 'contact@bureauexpress.ma', 'ESPECES', 0, 0);
