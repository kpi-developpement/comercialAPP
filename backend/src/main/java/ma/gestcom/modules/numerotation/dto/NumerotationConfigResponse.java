package ma.gestcom.modules.numerotation.dto;

import ma.gestcom.common.enums.TypeDocument;

/**
 * @param prochainNumero preview of the next number for the current year (not reserved)
 */
public record NumerotationConfigResponse(
        Long id,
        TypeDocument typeDocument,
        String libelle,
        String prefixe,
        Integer longueur,
        String prochainNumero) {
}
