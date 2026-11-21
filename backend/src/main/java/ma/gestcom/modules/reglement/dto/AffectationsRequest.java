package ma.gestcom.modules.reglement.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/**
 * Allocates (part of) the unallocated amount of an existing payment.
 */
public record AffectationsRequest(@NotEmpty @Valid List<AffectationRequest> affectations) {
}
