package ma.gestcom.modules.document.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

/**
 * Groups several delivery notes (or receptions) of the same tiers into one invoice.
 */
public record GroupementRequest(
        @NotEmpty(message = "Sélectionnez au moins un document") List<Long> documentIds,
        LocalDate dateDocument) {
}
