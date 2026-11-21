package ma.gestcom.modules.document.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.util.MoneyUtil;

/**
 * Header shared by every commercial document (sales and purchases).
 * The tiers is snapshotted (code, name, ICE, address) so a printed document never changes afterwards.
 *
 * @param <L> concrete line type
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractDocument<L extends AbstractDocumentLigne> extends BaseEntity {

    private static final EnumSet<StatutDocument> VALIDES =
            EnumSet.of(StatutDocument.VALIDE, StatutDocument.TRANSFORME_PARTIEL, StatutDocument.TRANSFORME);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeDocument type;

    /** Assigned at validation; drafts have no number. */
    @Column(unique = true, length = 30)
    private String numero;

    @Column(name = "date_document", nullable = false)
    private LocalDate dateDocument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutDocument statut = StatutDocument.BROUILLON;

    @Column(name = "tiers_id", nullable = false)
    private Long tiersId;

    @Column(name = "tiers_code", nullable = false, length = 30)
    private String tiersCode;

    @Column(name = "tiers_nom", nullable = false, length = 150)
    private String tiersNom;

    @Column(name = "tiers_ice", length = 15)
    private String tiersIce;

    @Column(name = "tiers_adresse")
    private String tiersAdresse;

    @Column(name = "depot_id", nullable = false)
    private Long depotId;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "total_ht", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalHt = BigDecimal.ZERO;

    @Column(name = "total_remise", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRemise = BigDecimal.ZERO;

    @Column(name = "total_tva", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalTva = BigDecimal.ZERO;

    @Column(name = "total_ttc", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalTtc = BigDecimal.ZERO;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "montant_regle", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRegle = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", length = 20)
    private StatutPaiement statutPaiement;

    /** Avoirs only: the credit note includes a physical return of goods (stock comes back). */
    @Column(name = "retour_marchandise", nullable = false)
    private boolean retourMarchandise;

    /** Human readable list of source document numbers, e.g. "BL-2026-00003, BL-2026-00004". */
    @Column(length = 500)
    private String sources;

    public abstract List<L> getLignes();

    public abstract void addLigne(L ligne);

    public boolean isValide() {
        return VALIDES.contains(statut);
    }

    public BigDecimal getResteAPayer() {
        return MoneyUtil.money(MoneyUtil.nz(totalTtc).subtract(MoneyUtil.nz(montantRegle)));
    }
}
