package ma.gestcom.modules.stock.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;

/**
 * Immutable journal line. {@code quantite} is signed: positive for an entry, negative for an exit.
 */
@Getter
@Setter
@Entity
@Table(name = "stock_mouvement")
public class StockMouvement extends BaseEntity {

    @Column(name = "date_mouvement", nullable = false)
    private Instant dateMouvement;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "depot_id", nullable = false)
    private Long depotId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeMouvement type;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite;

    @Column(name = "cout_unitaire", nullable = false, precision = 15, scale = 3)
    private BigDecimal coutUnitaire;

    @Column(name = "stock_apres", nullable = false, precision = 15, scale = 3)
    private BigDecimal stockApres;

    @Column(name = "pump_apres", nullable = false, precision = 15, scale = 3)
    private BigDecimal pumpApres;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 30)
    private TypeDocument referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_numero", length = 30)
    private String referenceNumero;

    private String libelle;
}
