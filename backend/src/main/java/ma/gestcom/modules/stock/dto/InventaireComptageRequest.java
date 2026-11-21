package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InventaireComptageRequest(
        @Size(max = 2000) String notes,
        @NotNull @Valid List<Ligne> lignes) {

    /** @param quantiteComptee null = not counted */
    public record Ligne(@NotNull Long ligneId, @DecimalMin("0") BigDecimal quantiteComptee) {
    }
}
