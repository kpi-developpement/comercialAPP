package ma.gestcom.modules.document.dto;

import java.time.LocalDate;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @param search        matches numero, tiers name/code or sources
 * @param nonSoldee     invoices only: keep those with resteAPayer > 0
 * @param echeanceAvant invoices only: due date strictly before this date (overdue list)
 * @param transformable keep validated documents that still have quantities to transform
 */
public record DocumentFilter(
        String search,
        Long tiersId,
        Long depotId,
        StatutDocument statut,
        StatutPaiement statutPaiement,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        Boolean nonSoldee,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate echeanceAvant,
        Boolean transformable) {

    public static DocumentFilter facturesNonSoldees(Long tiersId) {
        return new DocumentFilter(null, tiersId, null, null, null, null, null, true, null, null);
    }
}
