package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;

import ma.gestcom.common.enums.TypeReglement;

/**
 * Read-only tiers snapshot exposed to document and payment modules.
 *
 * @param plafondCredit 0 for suppliers and for clients without limit
 * @param bloque        clients only: account blocked for new sales documents
 * @param prospect      clients only: may only receive quotes
 */
public record TiersInfo(
        Long id,
        String code,
        String raisonSociale,
        String ice,
        String adresseComplete,
        Integer delaiPaiement,
        BigDecimal remiseDefaut,
        BigDecimal plafondCredit,
        TypeReglement modeReglementDefaut,
        boolean actif,
        boolean bloque,
        boolean prospect) {
}
