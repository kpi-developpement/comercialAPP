package ma.gestcom.modules.vente.service;

import java.util.List;

import ma.gestcom.modules.vente.dto.ClientVenteStats;

/** Products sold to a client, their invoice history and yearly revenue. */
public interface ClientVenteStatsService {

    List<ClientVenteStats.ProduitVendu> produitsVendus(Long clientId);

    List<ClientVenteStats.ProduitFacture> facturesProduit(Long clientId, Long articleId);

    /** Revenue TTC per year between two years (inclusive), years without sales included as 0. */
    List<ClientVenteStats.CaAnnuel> caAnnuel(Long clientId, int anneeDebut, int anneeFin);
}
