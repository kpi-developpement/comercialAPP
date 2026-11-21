package ma.gestcom.modules.parametre.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.SecteurSociete;
import ma.gestcom.common.enums.TypePersonne;

/**
 * Company identity printed on every document (single row).
 */
@Getter
@Setter
@Entity
@Table(name = "societe")
public class Societe extends BaseEntity {

    @Column(name = "raison_sociale", nullable = false, length = 150)
    private String raisonSociale;

    @Column(name = "forme_juridique", length = 30)
    private String formeJuridique;

    @Column(length = 15)
    private String ice;

    @Column(name = "identifiant_fiscal", length = 20)
    private String identifiantFiscal;

    @Column(length = 30)
    private String rc;

    @Column(length = 30)
    private String patente;

    @Column(length = 30)
    private String cnss;

    private String adresse;

    @Column(length = 80)
    private String ville;

    @Column(length = 30)
    private String telephone;

    @Column(length = 120)
    private String email;

    @Column(name = "site_web", length = 120)
    private String siteWeb;

    @Column(length = 80)
    private String banque;

    @Column(length = 30)
    private String rib;

    /** Logo as a data URL (image/png or image/jpeg, base64). */
    @Column(columnDefinition = "text")
    private String logo;

    @Column(nullable = false, length = 3)
    private String devise = "MAD";

    @Column(name = "pied_de_page", columnDefinition = "text")
    private String piedDePage;

    @Column(name = "autoriser_stock_negatif", nullable = false)
    private boolean autoriserStockNegatif;

    // --- Informations générales ---
    @Column(length = 20)
    private String code;

    @Column(length = 30)
    private String telephone2;

    @Column(length = 30)
    private String fax;

    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Column(name = "code_commune", length = 20)
    private String codeCommune;

    @Column(columnDefinition = "text")
    private String observation;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "date_debut_activite")
    private LocalDate dateDebutActivite;

    @Column(name = "date_fin_activite")
    private LocalDate dateFinActivite;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_personne", nullable = false, length = 10)
    private TypePersonne typePersonne = TypePersonne.MORALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SecteurSociete secteur = SecteurSociete.PRIVE;

    @Column(name = "loyer_annuel", nullable = false, precision = 15, scale = 2)
    private BigDecimal loyerAnnuel = BigDecimal.ZERO;

    // --- Entête ---
    /** Free text printed under the company name in the document header. */
    @Column(columnDefinition = "text")
    private String entete;

    // --- Informations juridiques ---
    @Column(length = 60)
    private String subdivision;

    @Column(name = "raf_reception", length = 60)
    private String rafReception;

    @Column(name = "ville_rc", length = 80)
    private String villeRc;

    @Column(length = 30)
    private String cnss2;

    @Column(name = "taux_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxCm = BigDecimal.ZERO;

    @Column(name = "taux_impot", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxImpot = BigDecimal.ZERO;

    @Column(name = "soumis_impot", nullable = false)
    private boolean soumisImpot;

    @Column(length = 150)
    private String activite;

    // --- Informations bancaires ---
    @Column(name = "agence_bancaire", length = 80)
    private String agenceBancaire;

    @Column(length = 20)
    private String swift;

    // --- Autres ---
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal capital = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer effectif = 0;

    // --- Personne physique ---
    @Column(name = "pp_nom", length = 80)
    private String ppNom;

    @Column(name = "pp_prenom", length = 80)
    private String ppPrenom;

    @Column(name = "pp_cin", length = 20)
    private String ppCin;

    @Column(name = "pp_qualite", length = 80)
    private String ppQualite;
}
