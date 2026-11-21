package ma.gestcom.modules.tiers.repository;

import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.entity.AbstractTiers;
import org.springframework.data.jpa.domain.Specification;

public final class TiersSpecifications {

    private TiersSpecifications() {
    }

    public static <T extends AbstractTiers> Specification<T> fromFilter(TiersFilter filter) {
        return Specification.allOf(
                SpecificationUtil.<T>isFalse("supprime"),
                SpecificationUtil.<T>containsAny(filter.search(), "code", "raisonSociale", "ice", "telephone"),
                SpecificationUtil.<T>containsAny(filter.ville(), "ville"),
                SpecificationUtil.<T>equalTo("actif", filter.actif()));
    }
}
