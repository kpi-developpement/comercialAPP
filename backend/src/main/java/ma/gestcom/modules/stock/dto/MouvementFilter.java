package ma.gestcom.modules.stock.dto;

import java.time.LocalDate;

import ma.gestcom.common.enums.TypeMouvement;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @param search matches the source document number
 */
public record MouvementFilter(
        Long articleId,
        Long depotId,
        TypeMouvement type,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
        String search) {
}
