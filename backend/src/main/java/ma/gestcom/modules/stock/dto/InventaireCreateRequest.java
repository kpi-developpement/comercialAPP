package ma.gestcom.modules.stock.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @param familleId optional: restrict the count to one family (and its sous-familles)
 */
public record InventaireCreateRequest(
        @NotNull Long depotId,
        @NotNull LocalDate dateInventaire,
        Long familleId,
        @Size(max = 2000) String notes) {
}
