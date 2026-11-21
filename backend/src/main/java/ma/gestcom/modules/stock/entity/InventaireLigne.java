package ma.gestcom.modules.stock.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "inventaire_ligne")
public class InventaireLigne extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventaire_id")
    private Inventaire inventaire;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "article_code", nullable = false, length = 30)
    private String articleCode;

    @Column(nullable = false)
    private String designation;

    @Column(name = "quantite_theorique", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantiteTheorique = BigDecimal.ZERO;

    /** Null while the article has not been counted yet (no adjustment generated). */
    @Column(name = "quantite_comptee", precision = 15, scale = 3)
    private BigDecimal quantiteComptee;

    public BigDecimal getEcart() {
        return quantiteComptee == null ? null : quantiteComptee.subtract(quantiteTheorique);
    }
}
