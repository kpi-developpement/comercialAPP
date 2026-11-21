package ma.gestcom.modules.article.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.article.dto.ArticleFilter;
import ma.gestcom.modules.article.entity.Article;
import ma.gestcom.modules.article.entity.Famille;
import org.springframework.data.jpa.domain.Specification;

public final class ArticleSpecifications {

    private ArticleSpecifications() {
    }

    public static Specification<Article> fromFilter(ArticleFilter filter) {
        return Specification.allOf(
                SpecificationUtil.isFalse("supprime"),
                SpecificationUtil.containsAny(filter.search(), "code", "designation", "codeBarres"),
                inFamille(filter.familleId()),
                SpecificationUtil.equalTo("type", filter.type()),
                SpecificationUtil.equalTo("actif", filter.actif()));
    }

    /** Matches the family itself and its sous-familles. */
    private static Specification<Article> inFamille(Long familleId) {
        if (familleId == null) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Article, Famille> famille = root.join("famille", JoinType.LEFT);
            Join<Famille, Famille> parent = famille.join("parent", JoinType.LEFT);
            return cb.or(cb.equal(famille.get("id"), familleId), cb.equal(parent.get("id"), familleId));
        };
    }
}
