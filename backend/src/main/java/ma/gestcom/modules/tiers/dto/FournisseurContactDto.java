package ma.gestcom.modules.tiers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Supplier contact ("interlocuteur"), used both in requests and responses. */
public record FournisseurContactDto(
        Long id,
        @NotBlank(message = "Nom de l'interlocuteur obligatoire") @Size(max = 80) String nom,
        @Size(max = 80) String prenom,
        @Size(max = 80) String fonction,
        @Size(max = 30) String telephone,
        @Email @Size(max = 120) String email) {
}
