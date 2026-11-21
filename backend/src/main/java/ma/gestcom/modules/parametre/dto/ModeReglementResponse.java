package ma.gestcom.modules.parametre.dto;

import ma.gestcom.common.enums.TypeReglement;

public record ModeReglementResponse(Long id, TypeReglement code, String libelle, boolean actif) {
}
