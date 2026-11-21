package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;

/**
 * Invoiced amounts of a tiers, used to compute its balance.
 *
 * @param totalFactures TTC of validated invoices
 * @param totalAvoirs   TTC of validated credit notes
 * @param totalRegle    part of the invoices already settled
 */
public record TiersEncours(BigDecimal totalFactures, BigDecimal totalAvoirs, BigDecimal totalRegle) {
}
