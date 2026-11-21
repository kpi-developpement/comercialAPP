package ma.gestcom.modules.document.service;

import java.util.EnumSet;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.entity.AbstractDocument;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters common to sales and purchase documents.
 */
public final class DocumentSpecifications {

    private static final EnumSet<StatutDocument> VALIDES =
            EnumSet.of(StatutDocument.VALIDE, StatutDocument.TRANSFORME_PARTIEL, StatutDocument.TRANSFORME);

    private DocumentSpecifications() {
    }

    public static <T extends AbstractDocument<?>> Specification<T> fromFilter(TypeDocument type, DocumentFilter f) {
        return Specification.allOf(
                SpecificationUtil.<T>equalTo("type", type),
                SpecificationUtil.<T>containsAny(f.search(), "numero", "tiersNom", "tiersCode", "sources"),
                SpecificationUtil.<T>equalTo("tiersId", f.tiersId()),
                SpecificationUtil.<T>equalTo("depotId", f.depotId()),
                SpecificationUtil.<T>equalTo("statut", f.statut()),
                SpecificationUtil.<T>equalTo("statutPaiement", f.statutPaiement()),
                SpecificationUtil.<T>dateBetween("dateDocument", f.dateDebut(), f.dateFin()),
                Boolean.TRUE.equals(f.nonSoldee()) ? nonSoldee() : null,
                f.echeanceAvant() == null ? null
                        : (root, q, cb) -> cb.lessThan(root.get("dateEcheance"), f.echeanceAvant()),
                Boolean.TRUE.equals(f.transformable()) ? transformable() : null);
    }

    public static <T extends AbstractDocument<?>> Specification<T> nonSoldee() {
        return (root, q, cb) -> cb.and(root.get("statut").in(VALIDES),
                cb.lessThan(root.get("montantRegle"), root.get("totalTtc")));
    }

    private static <T extends AbstractDocument<?>> Specification<T> transformable() {
        return (root, q, cb) -> root.get("statut").in(StatutDocument.VALIDE, StatutDocument.TRANSFORME_PARTIEL);
    }
}
