package ma.gestcom.modules.tiers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ma.gestcom.common.entity.BaseEntity;

/** Product suggested to a client ("catalogue des produits suggérés"). */
@Getter
@Setter
@Entity
@Table(name = "client_article_suggere")
public class ClientArticleSuggere extends BaseEntity {

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;
}
