package ma.gestcom.modules.tiers.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.enums.TypeFournisseur;

@Getter
@Setter
@Entity
@Table(name = "fournisseur")
public class Fournisseur extends AbstractTiers {

    @Column(length = 60)
    private String nationalite;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_fournisseur", nullable = false, length = 20)
    private TypeFournisseur typeFournisseur = TypeFournisseur.A_TERME;

    @Column(name = "forme_juridique", length = 30)
    private String formeJuridique;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal capital = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer effectif = 0;

    @Column(name = "non_resident", nullable = false)
    private boolean nonResident;

    @Column(nullable = false)
    private boolean commercant;

    /** Purchase requests must be approved by a supervisor. */
    @Column(name = "validation_superviseur", nullable = false)
    private boolean validationSuperviseur;

    @Column(name = "delai_livraison_min", nullable = false)
    private Integer delaiLivraisonMin = 0;

    @Column(name = "delai_livraison_max", nullable = false)
    private Integer delaiLivraisonMax = 0;

    @Column(name = "condition_paiement", length = 80)
    private String conditionPaiement;

    @Column(name = "commentaire_paiement", columnDefinition = "text")
    private String commentairePaiement;

    @Column(name = "plafond_decouvert", nullable = false, precision = 15, scale = 2)
    private BigDecimal plafondDecouvert = BigDecimal.ZERO;

    @Column(name = "plafond_remise", nullable = false, precision = 5, scale = 2)
    private BigDecimal plafondRemise = BigDecimal.ZERO;

    @Column(name = "imputation_tva", length = 30)
    private String imputationTva;

    @Column(name = "code_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal codeTva = BigDecimal.ZERO;

    @Column(name = "code_integration", length = 30)
    private String codeIntegration;

    /** Attestation de régularité fiscale (DGI). */
    @Column(name = "attestation_numero", length = 50)
    private String attestationNumero;

    @Column(name = "attestation_date")
    private LocalDate attestationDate;

    @Column(name = "attestation_validite")
    private LocalDate attestationValidite;

    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<FournisseurContact> contacts = new ArrayList<>();

    /** Tax situation is regular while the attestation is still valid. */
    public boolean isSituationFiscaleReguliere() {
        return attestationValidite != null && !attestationValidite.isBefore(LocalDate.now());
    }
}
