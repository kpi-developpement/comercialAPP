package ma.gestcom.modules.stock.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

/**
 * Current quantity of an article in a depot. Only {@code StockService} writes to this table.
 */
@Getter
@Setter
@Entity
@Table(name = "stock_article_depot")
public class StockArticleDepot extends BaseEntity {

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "depot_id", nullable = false)
    private Long depotId;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite = BigDecimal.ZERO;
}
