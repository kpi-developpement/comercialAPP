package ma.gestcom.modules.stock.dto;

public record DepotResponse(Long id, String code, String libelle, String adresse, boolean actif, boolean parDefaut) {
}
