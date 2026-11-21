package ma.gestcom.modules.reglement.service;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.achat.service.DocumentAchatService;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.document.dto.TiersEncours;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.service.ClientService;
import ma.gestcom.modules.tiers.service.FournisseurService;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Routes invoice and tiers operations to the sales or purchase module according to the payment direction.
 */
@Component
@RequiredArgsConstructor
public class FactureGateway {

    private final DocumentVenteService venteService;
    private final DocumentAchatService achatService;
    private final ClientService clientService;
    private final FournisseurService fournisseurService;

    public TiersInfo tiers(SensTiers sens, Long id) {
        return sens == SensTiers.CLIENT ? clientService.getInfo(id) : fournisseurService.getInfo(id);
    }

    public FactureInfo facture(SensTiers sens, Long id) {
        return sens == SensTiers.CLIENT ? venteService.getFactureInfo(id) : achatService.getFactureInfo(id);
    }

    public void appliquer(SensTiers sens, Long factureId, BigDecimal delta) {
        if (sens == SensTiers.CLIENT) {
            venteService.appliquerReglement(factureId, delta);
        } else {
            achatService.appliquerReglement(factureId, delta);
        }
    }

    public TiersEncours encours(SensTiers sens, Long tiersId) {
        return sens == SensTiers.CLIENT ? venteService.encours(tiersId) : achatService.encours(tiersId);
    }

    public List<DocumentSummaryResponse> nonSoldees(SensTiers sens, Long tiersId) {
        return sens == SensTiers.CLIENT ? venteService.facturesNonSoldees(tiersId)
                : achatService.facturesNonSoldees(tiersId);
    }

    public PageResponse<DocumentSummaryResponse> rechercherFactures(SensTiers sens, DocumentFilter filter,
                                                                    Pageable pageable) {
        return sens == SensTiers.CLIENT
                ? venteService.search(TypeDocument.FACTURE, filter, pageable)
                : achatService.search(TypeDocument.FACTURE_FOURNISSEUR, filter, pageable);
    }
}
