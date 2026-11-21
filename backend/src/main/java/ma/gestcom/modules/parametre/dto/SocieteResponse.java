package ma.gestcom.modules.parametre.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import ma.gestcom.common.enums.SecteurSociete;
import ma.gestcom.common.enums.TypePersonne;

public record SocieteResponse(
        Long id,
        String code,
        String raisonSociale,
        String ice,
        String adresse,
        String ville,
        String codePostal,
        String telephone,
        String telephone2,
        String fax,
        String codeCommune,
        String email,
        String siteWeb,
        String observation,
        LocalDate dateCreation,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        TypePersonne typePersonne,
        SecteurSociete secteur,
        BigDecimal loyerAnnuel,
        String logo,
        String entete,
        String piedDePage,
        String formeJuridique,
        String patente,
        String identifiantFiscal,
        String subdivision,
        String rc,
        String villeRc,
        String cnss,
        String cnss2,
        String rafReception,
        BigDecimal tauxCm,
        BigDecimal tauxImpot,
        boolean soumisImpot,
        String activite,
        String banque,
        String agenceBancaire,
        String rib,
        String swift,
        String devise,
        BigDecimal capital,
        Integer effectif,
        boolean autoriserStockNegatif,
        String ppNom,
        String ppPrenom,
        String ppCin,
        String ppQualite) {
}
