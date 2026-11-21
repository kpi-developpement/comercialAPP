package ma.gestcom.modules.document.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

/**
 * Line shared by every commercial document. Amounts are computed by {@code DocumentCalculator}.
 * {@code quantiteRestante} is the quantity not yet transformed into a next document;
 * {@code sourceLigneId} links the line to the line it was transformed from.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractDocumentLigne extends BaseEntity {

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "article_code", length = 30)
    private String articleCode;

    @Column(nullable = false)
    private String designation;

    @Column(length = 20)
    private String unite;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire_ht", nullable = false, precision = 15, scale = 3)
    private BigDecimal prixUnitaireHt;

    @Column(name = "remise_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal remisePct = BigDecimal.ZERO;

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva = BigDecimal.ZERO;

    @Column(name = "montant_remise", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    /** Net HT amount (after discount). */
    @Column(name = "montant_ht", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantHt = BigDecimal.ZERO;

    @Column(name = "montant_tva", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTva = BigDecimal.ZERO;

    @Column(name = "montant_ttc", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTtc = BigDecimal.ZERO;

    @Column(name = "quantite_restante", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantiteRestante = BigDecimal.ZERO;

    @Column(name = "source_ligne_id")
    private Long sourceLigneId;

    @Column(name = "source_document_id")
    private Long sourceDocumentId;

    @Column(name = "article_stocke", nullable = false)
    private boolean articleStocke;

    /** True once this line has generated a stock movement (guarantees no double movement). */
    @Column(name = "impacte_stock", nullable = false)
    private boolean impacteStock;

    /** Sales: PUMP at validation (gross margin). Purchases: net unit cost (PUMP input). */
    @Column(name = "cout_unitaire", nullable = false, precision = 15, scale = 3)
    private BigDecimal coutUnitaire = BigDecimal.ZERO;

    public abstract AbstractDocument<?> getDocument();
}
