package ma.gestcom.modules.tiers.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.TypeReglement;

/**
 * Fields shared by clients and fournisseurs (Moroccan legal identifiers included).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractTiers extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "raison_sociale", nullable = false, length = 150)
    private String raisonSociale;

    @Column(length = 15)
    private String ice;

    @Column(name = "identifiant_fiscal", length = 20)
    private String identifiantFiscal;

    @Column(length = 30)
    private String rc;

    @Column(length = 30)
    private String patente;

    private String adresse;

    @Column(length = 80)
    private String ville;

    @Column(length = 30)
    private String telephone;

    @Column(length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_reglement_defaut", length = 20)
    private TypeReglement modeReglementDefaut;

    @Column(name = "delai_paiement", nullable = false)
    private Integer delaiPaiement = 0;

    @Column(name = "remise_defaut", nullable = false, precision = 5, scale = 2)
    private BigDecimal remiseDefaut = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "code_groupe", length = 30)
    private String codeGroupe;

    @Column(nullable = false, length = 60)
    private String pays = "Maroc";

    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Column(length = 30)
    private String telephone2;

    @Column(length = 30)
    private String fax;

    @Column(name = "site_web", length = 120)
    private String siteWeb;

    @Column(nullable = false, length = 3)
    private String devise = "MAD";

    @Column(length = 150)
    private String activite;

    @Column(length = 30)
    private String cnss;

    @Column(length = 60)
    private String categorie;

    @Column(name = "mode_livraison", length = 60)
    private String modeLivraison;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal escompte = BigDecimal.ZERO;

    @Column(name = "date_debut_activite")
    private LocalDate dateDebutActivite;

    @Column(name = "date_fin_activite")
    private LocalDate dateFinActivite;

    @Column(name = "date_creation_societe")
    private LocalDate dateCreationSociete;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(nullable = false)
    private boolean supprime;

    public String getAdresseComplete() {
        if (ville == null || ville.isBlank()) {
            return adresse;
        }
        return adresse == null || adresse.isBlank() ? ville : adresse + ", " + ville;
    }
}
