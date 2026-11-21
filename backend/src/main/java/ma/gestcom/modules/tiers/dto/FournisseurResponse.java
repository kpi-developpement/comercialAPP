package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.TypeFournisseur;
import ma.gestcom.common.enums.TypeReglement;

public record FournisseurResponse(
        Long id,
        String code,
        String raisonSociale,
        String codeGroupe,
        TypeFournisseur typeFournisseur,
        String adresse,
        String pays,
        String ville,
        String codePostal,
        String nationalite,
        String telephone,
        String telephone2,
        String fax,
        String email,
        String siteWeb,
        String activite,
        String devise,
        String categorie,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        String notes,
        boolean actif,
        List<FournisseurContactDto> contacts,
        String formeJuridique,
        String rc,
        String patente,
        BigDecimal capital,
        String ice,
        String identifiantFiscal,
        String cnss,
        Integer effectif,
        boolean nonResident,
        boolean commercant,
        LocalDate dateCreationSociete,
        boolean validationSuperviseur,
        String modeLivraison,
        Integer delaiLivraisonMin,
        Integer delaiLivraisonMax,
        TypeReglement modeReglementDefaut,
        String conditionPaiement,
        String commentairePaiement,
        BigDecimal plafondDecouvert,
        Integer delaiPaiement,
        BigDecimal plafondRemise,
        BigDecimal remiseDefaut,
        BigDecimal escompte,
        String imputationTva,
        BigDecimal codeTva,
        String codeIntegration,
        String attestationNumero,
        LocalDate attestationDate,
        LocalDate attestationValidite,
        boolean situationFiscaleReguliere,
        Instant createdAt) {
}
