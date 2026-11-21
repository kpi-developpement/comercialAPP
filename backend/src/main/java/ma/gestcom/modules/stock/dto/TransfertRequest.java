package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransfertRequest(
        @NotNull Long depotSourceId,
        @NotNull Long depotDestinationId,
        @NotNull LocalDate dateTransfert,
        @Size(max = 2000) String notes,
        @NotEmpty(message = "Ajoutez au moins une ligne") @Valid List<Ligne> lignes) {

    public record Ligne(@NotNull Long articleId,
                        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal quantite) {
    }
}
