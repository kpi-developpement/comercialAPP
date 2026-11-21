package ma.gestcom.modules.parametre.dto;

import java.math.BigDecimal;

public record TauxTvaResponse(Long id, BigDecimal taux, String libelle, boolean actif, boolean parDefaut) {
}
