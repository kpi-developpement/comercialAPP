package ma.gestcom.modules.tiers.dto;

/**
 * List row for clients and suppliers. {@code prospect} / {@code bloque} are null for suppliers.
 */
public record TiersSummaryResponse(
        Long id,
        String code,
        String raisonSociale,
        String ice,
        String ville,
        String telephone,
        String email,
        boolean actif,
        Boolean prospect,
        Boolean bloque) {
}
