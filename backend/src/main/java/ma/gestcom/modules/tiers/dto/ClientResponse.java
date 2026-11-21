package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import ma.gestcom.common.enums.RisqueClient;
import ma.gestcom.common.enums.TypeClient;
import ma.gestcom.common.enums.TypeReglement;

public record ClientResponse(
        Long id,
        String code,
        String raisonSociale,
        boolean prospect,
        boolean bloque,
        boolean arbitrage,
        boolean groupeSocietes,
        String codeGroupe,
        TypeClient typeClient,
        RisqueClient risque,
        String ice,
        String identifiantFiscal,
        String rc,
        String patente,
        String cnss,
        String cin,
        String adresse,
        String pays,
        String ville,
        String codePostal,
        String telephone,
        String telephone2,
        String fax,
        String email,
        String siteWeb,
        String devise,
        String representant,
        Long depotId,
        String activite,
        String categorie,
        String journal,
        String compteComptable,
        String emailRelance,
        String modeLivraison,
        TypeReglement modeReglementDefaut,
        Integer delaiPaiement,
        BigDecimal remiseDefaut,
        BigDecimal escompte,
        BigDecimal plafondCredit,
        LocalDate dateDebutProspect,
        LocalDate dateDebutActivite,
        LocalDate dateFinActivite,
        LocalDate dateCreationSociete,
        LocalDate dateFinSociete,
        String notes,
        boolean actif,
        Instant createdAt) {
}
