package ma.gestcom.modules.article.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.ArticleType;

public record ArticleRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 200) String designation,
        Long familleId,
        @NotBlank @Size(max = 20) String unite,
        @NotNull @DecimalMin("0") @Digits(integer = 12, fraction = 3) BigDecimal prixAchatHt,
        @NotNull @DecimalMin("0") @Digits(integer = 12, fraction = 3) BigDecimal prixVenteHt,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal tauxTva,
        @Size(max = 50) String codeBarres,
        @NotNull @DecimalMin("0") @Digits(integer = 12, fraction = 3) BigDecimal stockMinimum,
        boolean actif,
        @NotNull ArticleType type) {
}
