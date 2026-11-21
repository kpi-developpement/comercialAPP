package ma.gestcom.modules.reglement.dto;

import jakarta.validation.constraints.NotNull;
import ma.gestcom.common.enums.StatutReglement;

public record StatutReglementRequest(@NotNull StatutReglement statut) {
}
