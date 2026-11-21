package ma.gestcom.common.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * Small reusable building blocks for JPA Specifications. Each returns {@code null} (ignored by
 * {@link Specification#allOf}) when the filter value is empty.
 */
public final class SpecificationUtil {

    private SpecificationUtil() {
    }

    /** Case-insensitive "contains" on any of the given attributes (dotted paths allowed, e.g. "famille.libelle"). */
    public static <T> Specification<T> containsAny(String term, String... attributes) {
        if (term == null || term.isBlank()) {
            return null;
        }
        String like = "%" + term.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.or(Arrays.stream(attributes)
                .map(attr -> cb.like(cb.lower(path(root, attr).as(String.class)), like))
                .toArray(Predicate[]::new));
    }

    public static <T> Specification<T> equalTo(String attribute, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(path(root, attribute), value);
    }

    public static <T> Specification<T> dateBetween(String attribute, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return null;
        }
        return (root, query, cb) -> {
            Path<LocalDate> p = path(root, attribute);
            if (from != null && to != null) {
                return cb.between(p, from, to);
            }
            return from != null ? cb.greaterThanOrEqualTo(p, from) : cb.lessThanOrEqualTo(p, to);
        };
    }

    public static <T> Specification<T> isFalse(String attribute) {
        return (root, query, cb) -> cb.isFalse(path(root, attribute));
    }

    @SuppressWarnings("unchecked")
    public static <Y> Path<Y> path(Root<?> root, String dotted) {
        Path<?> p = root;
        for (String part : dotted.split("\\.")) {
            p = p.get(part);
        }
        return (Path<Y>) p;
    }
}
