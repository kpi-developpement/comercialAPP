package ma.gestcom.modules.article.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;
import ma.gestcom.common.enums.ArticleType;

@Getter
@Setter
@Entity
@Table(name = "article")
public class Article extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "famille_id")
    private Famille famille;

    @Column(nullable = false, length = 20)
    private String unite;

    @Column(name = "prix_achat_ht", nullable = false, precision = 15, scale = 3)
    private BigDecimal prixAchatHt = BigDecimal.ZERO;

    @Column(name = "prix_vente_ht", nullable = false, precision = 15, scale = 3)
    private BigDecimal prixVenteHt = BigDecimal.ZERO;

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTva;

    @Column(name = "code_barres", unique = true, length = 50)
    private String codeBarres;

    @Column(name = "stock_minimum", nullable = false, precision = 15, scale = 3)
    private BigDecimal stockMinimum = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ArticleType type = ArticleType.STOCKE;

    /** Prix unitaire moyen pondéré, global à tous les dépôts. Maintained only by the stock module. */
    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal pump = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean supprime;
}
