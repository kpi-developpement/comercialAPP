package ma.gestcom.modules.vente.controller;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.vente.dto.ClientVenteStats;
import ma.gestcom.modules.vente.service.ClientVenteStatsService;
import ma.gestcom.security.Roles;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** "Produits vendus par client" tab of the client file. */
@RestController
@RequestMapping("/api/v1/ventes/clients/{clientId}")
@RequiredArgsConstructor
@PreAuthorize(Roles.VENTE_READ)
public class ClientVenteStatsController {

    private final ClientVenteStatsService service;

    @GetMapping("/produits")
    public List<ClientVenteStats.ProduitVendu> produits(@PathVariable Long clientId) {
        return service.produitsVendus(clientId);
    }

    @GetMapping("/produits/{articleId}/factures")
    public List<ClientVenteStats.ProduitFacture> factures(@PathVariable Long clientId, @PathVariable Long articleId) {
        return service.facturesProduit(clientId, articleId);
    }

    @GetMapping("/ca-annuel")
    public List<ClientVenteStats.CaAnnuel> caAnnuel(@PathVariable Long clientId,
                                                    @RequestParam(required = false) Integer de,
                                                    @RequestParam(required = false) Integer a) {
        int fin = a != null ? a : LocalDate.now().getYear();
        int debut = de != null ? de : fin - 2;
        return service.caAnnuel(clientId, debut, fin);
    }
}
