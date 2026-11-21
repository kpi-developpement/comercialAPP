package ma.gestcom.modules.reglement.entity;

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
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeReglement;
import ma.gestcom.common.util.MoneyUtil;

/**
 * A payment received from a client or paid to a supplier, allocated to one or many invoices.
 */
@Getter
@Setter
@Entity
@Table(name = "reglement")
public class Reglement extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SensTiers sens;

    @Column(name = "tiers_id", nullable = false)
    private Long tiersId;

    @Column(name = "tiers_nom", nullable = false, length = 150)
    private String tiersNom;

    @Column(name = "date_reglement", nullable = false)
    private LocalDate dateReglement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeReglement mode;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "montant_affecte", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantAffecte = BigDecimal.ZERO;

    /** Chèque / effet number or transfer reference. */
    @Column(length = 50)
    private String reference;

    @Column(length = 80)
    private String banque;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutReglement statut;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "reglement", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ReglementAffectation> affectations = new ArrayList<>();

    public void addAffectation(ReglementAffectation affectation) {
        affectation.setReglement(this);
        affectations.add(affectation);
        montantAffecte = MoneyUtil.money(montantAffecte.add(affectation.getMontant()));
    }

    public BigDecimal getMontantNonAffecte() {
        return MoneyUtil.money(montant.subtract(montantAffecte));
    }
}
