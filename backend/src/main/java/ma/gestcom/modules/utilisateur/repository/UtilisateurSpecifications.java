package ma.gestcom.modules.utilisateur.repository;

import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.utilisateur.dto.UtilisateurFilter;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import org.springframework.data.jpa.domain.Specification;

public final class UtilisateurSpecifications {

    private UtilisateurSpecifications() {
    }

    public static Specification<Utilisateur> fromFilter(UtilisateurFilter filter) {
        return Specification.allOf(
                SpecificationUtil.<Utilisateur>containsAny(filter.search(), "username", "nomComplet", "email"),
                SpecificationUtil.<Utilisateur>equalTo("role", filter.role()),
                SpecificationUtil.<Utilisateur>equalTo("actif", filter.actif()));
    }
}
