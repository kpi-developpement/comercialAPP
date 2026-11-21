package ma.gestcom.modules.stock.dto;

import java.time.LocalDate;

import ma.gestcom.common.enums.StatutOperation;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Filter shared by inventaires and transferts lists.
 */
public record OperationFilter(
        String search,
        Long depotId,
        StatutOperation statut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
}
