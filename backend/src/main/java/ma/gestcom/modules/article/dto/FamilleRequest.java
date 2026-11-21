package ma.gestcom.modules.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FamilleRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 120) String libelle,
        Long parentId) {
}
