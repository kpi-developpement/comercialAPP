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
@Table(name = "transfert_ligne")
public class TransfertLigne extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfert_id")
    private Transfert transfert;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "article_code", nullable = false, length = 30)
    private String articleCode;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite;
}
