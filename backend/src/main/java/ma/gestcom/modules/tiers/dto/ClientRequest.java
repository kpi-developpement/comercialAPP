package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.RisqueClient;
import ma.gestcom.common.enums.TypeClient;
import ma.gestcom.common.enums.TypeReglement;

/**
 * "Fichier client": fiche (identity, contact, legal ids, commercial terms) + autres informations.
 * {@code plafondCredit} is the "découvert", {@code delaiPaiement} the "durée crédit", {@code patente} the T.P.
 */
public record ClientRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 150) String raisonSociale,
        boolean prospect,
        boolean bloque,
        boolean arbitrage,
        boolean groupeSocietes,
        @Size(max = 30) String codeGroupe,
        @NotNull TypeClient typeClient,
        @NotNull RisqueClient risque,
        @Pattern(regexp = "^$|^\\d{15}$", message = "L'ICE doit contenir 15 chiffres") String ice,
        @Size(max = 20) String identifiantFiscal,
        @Size(max = 30) String rc,
        @Size(max = 30) String patente,
        @Size(max = 30) String cnss,
        @Size(max = 20) String cin,
        @Size(max = 255) String adresse,
        @NotBlank @Size(max = 60) String pays,
        @Size(max = 80) String ville,
        @Size(max = 10) String codePostal,
        @Size(max = 30) String telephone,
        @Size(max = 30) String telephone2,
        @Size(max = 30) String fax,
        @Email @Size(max = 120) String email,
        @Size(max = 120) String siteWeb,
        @NotBlank @Size(min = 3, max = 3) String devise,
        @Size(max = 80) String representant,
        Long depotId,
        @Size(max = 150) String activite,
        @Size(max = 60) String categorie,
        @Size(max = 30) String journal,
        @Size(max = 30) String compteComptable,
        @Email @Size(max = 120) String emailRelance,
        @Size(max = 60) String modeLivraison,
        TypeReglement modeReglementDefaut,
        @NotNull @Min(0) @Max(365) Integer delaiPaiement,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal remiseDefaut,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal escompte,
        @NotNull @DecimalMin("0") BigDecimal plafondCredit,
        LocalDate dateDebutProspect,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        LocalDate dateCreationSociete,
        LocalDate dateFinSociete,
        @Size(max = 2000) String notes,
        boolean actif) {
}
