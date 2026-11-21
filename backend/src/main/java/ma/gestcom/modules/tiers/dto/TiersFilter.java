package ma.gestcom.modules.tiers.dto;

/**
 * @param search matches code, raison sociale, ICE or téléphone
 */
public record TiersFilter(String search, String ville, Boolean actif) {
}
