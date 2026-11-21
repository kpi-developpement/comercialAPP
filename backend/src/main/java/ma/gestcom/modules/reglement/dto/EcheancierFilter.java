package ma.gestcom.modules.reglement.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @param echeanceAvant due date strictly before (defaults to "no limit"; use today for overdue invoices)
 * @param enRetard      shortcut for echeanceAvant = today
 */
public record EcheancierFilter(
        String search,
        Long tiersId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate echeanceAvant,
        Boolean enRetard) {
}
