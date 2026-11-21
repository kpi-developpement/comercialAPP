package ma.gestcom.modules.reglement.dto;

import java.time.LocalDate;

import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeReglement;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @param search matches numero, tiers name, reference or bank
 */
public record ReglementFilter(
        String search,
        Long tiersId,
        TypeReglement mode,
        StatutReglement statut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
}
