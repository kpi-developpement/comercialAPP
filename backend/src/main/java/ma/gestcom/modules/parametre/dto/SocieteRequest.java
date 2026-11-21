package ma.gestcom.modules.parametre.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.SecteurSociete;
import ma.gestcom.common.enums.TypePersonne;

/** "Fichier société" — every tab of the screen. {@code patente} is the "taxe professionnelle". */
public record SocieteRequest(
        @Size(max = 20) String code,
        @NotBlank @Size(max = 150) String raisonSociale,
        // Informations générales
        @Pattern(regexp = "^$|^\\d{15}$", message = "L'ICE doit contenir 15 chiffres") String ice,
        @Size(max = 255) String adresse,
        @Size(max = 80) String ville,
        @Size(max = 10) String codePostal,
        @Size(max = 30) String telephone,
        @Size(max = 30) String telephone2,
        @Size(max = 30) String fax,
        @Size(max = 20) String codeCommune,
        @Email @Size(max = 120) String email,
        @Size(max = 120) String siteWeb,
        @Size(max = 2000) String observation,
        LocalDate dateCreation,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        @NotNull TypePersonne typePersonne,
        @NotNull SecteurSociete secteur,
        @NotNull @DecimalMin("0") BigDecimal loyerAnnuel,
        // Entête et logo
        @Size(max = 700_000, message = "Logo trop volumineux (500 Ko max)") String logo,
        @Size(max = 2000) String entete,
        @Size(max = 2000) String piedDePage,
        // Informations juridiques
        @Size(max = 30) String formeJuridique,
        @Size(max = 30) String patente,
        @Size(max = 20) String identifiantFiscal,
        @Size(max = 60) String subdivision,
        @Size(max = 30) String rc,
        @Size(max = 80) String villeRc,
        @Size(max = 30) String cnss,
        @Size(max = 30) String cnss2,
        @Size(max = 60) String rafReception,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal tauxCm,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal tauxImpot,
        boolean soumisImpot,
        @Size(max = 150) String activite,
        // Informations bancaires
        @Size(max = 80) String banque,
        @Size(max = 80) String agenceBancaire,
        @Pattern(regexp = "^$|^\\d{24}$", message = "Le RIB doit contenir 24 chiffres") String rib,
        @Size(max = 20) String swift,
        // Autres
        @NotBlank @Size(min = 3, max = 3) String devise,
        @NotNull @DecimalMin("0") BigDecimal capital,
        @NotNull @Min(0) Integer effectif,
        boolean autoriserStockNegatif,
        // Personne physique
        @Size(max = 80) String ppNom,
        @Size(max = 80) String ppPrenom,
        @Size(max = 20) String ppCin,
        @Size(max = 80) String ppQualite) {
}
