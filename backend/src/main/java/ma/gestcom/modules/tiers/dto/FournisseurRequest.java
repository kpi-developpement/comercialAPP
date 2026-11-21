package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.TypeFournisseur;
import ma.gestcom.common.enums.TypeReglement;

/**
 * "Fichier fournisseur": information générale + interlocuteurs, informations légales and the
 * attestation de régularité fiscale. {@code delaiPaiement} is the "durée de crédit maximale".
 */
public record FournisseurRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String raisonSociale,
        @Size(max = 30) String codeGroupe,
        @NotNull TypeFournisseur typeFournisseur,
        @Size(max = 255) String adresse,
        @NotBlank @Size(max = 60) String pays,
        @Size(max = 80) String ville,
        @Size(max = 10) String codePostal,
        @Size(max = 60) String nationalite,
        @Size(max = 30) String telephone,
        @Size(max = 30) String telephone2,
        @Size(max = 30) String fax,
        @Email @Size(max = 120) String email,
        @Size(max = 120) String siteWeb,
        @Size(max = 150) String activite,
        @NotBlank @Size(min = 3, max = 3) String devise,
        @Size(max = 60) String categorie,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        @Size(max = 2000) String notes,
        boolean actif,
        @Valid List<FournisseurContactDto> contacts,
        // informations légales
        @Size(max = 30) String formeJuridique,
        @Size(max = 30) String rc,
        @Size(max = 30) String patente,
        @NotNull @DecimalMin("0") BigDecimal capital,
        @Pattern(regexp = "^$|^\\d{15}$", message = "L'ICE doit contenir 15 chiffres") String ice,
        @Size(max = 20) String identifiantFiscal,
        @Size(max = 30) String cnss,
        @NotNull @Min(0) Integer effectif,
        boolean nonResident,
        boolean commercant,
        LocalDate dateCreationSociete,
        boolean validationSuperviseur,
        @Size(max = 60) String modeLivraison,
        @NotNull @Min(0) @Max(365) Integer delaiLivraisonMin,
        @NotNull @Min(0) @Max(365) Integer delaiLivraisonMax,
        TypeReglement modeReglementDefaut,
        @Size(max = 80) String conditionPaiement,
        @Size(max = 2000) String commentairePaiement,
        @NotNull @DecimalMin("0") BigDecimal plafondDecouvert,
        @NotNull @Min(0) @Max(365) Integer delaiPaiement,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal plafondRemise,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal remiseDefaut,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal escompte,
        @Size(max = 30) String imputationTva,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal codeTva,
        @Size(max = 30) String codeIntegration,
        // attestation de régularité fiscale
        @Size(max = 50) String attestationNumero,
        LocalDate attestationDate,
        LocalDate attestationValidite) {
}
