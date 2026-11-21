package ma.gestcom.modules.reglement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.TiersEncours;
import ma.gestcom.modules.reglement.dto.EcheancierFilter;
import ma.gestcom.modules.reglement.dto.SoldeTiersResponse;
import ma.gestcom.modules.reglement.repository.ReglementRepository;
import ma.gestcom.modules.reglement.service.FactureGateway;
import ma.gestcom.modules.reglement.service.SoldeService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoldeServiceImpl implements SoldeService {

    private final FactureGateway factures;
    private final ReglementRepository reglementRepository;

    @Override
    public SoldeTiersResponse solde(SensTiers sens, Long tiersId) {
        factures.tiers(sens, tiersId);
        TiersEncours e = factures.encours(sens, tiersId);
        BigDecimal nonAffecte = reglementRepository.sumNonAffecte(sens, tiersId);
        BigDecimal solde = e.totalFactures().subtract(e.totalAvoirs()).subtract(e.totalRegle()).subtract(nonAffecte);
        return new SoldeTiersResponse(tiersId, MoneyUtil.money(e.totalFactures()), MoneyUtil.money(e.totalAvoirs()),
                MoneyUtil.money(e.totalRegle()), MoneyUtil.money(nonAffecte), MoneyUtil.money(solde),
                factures.nonSoldees(sens, tiersId));
    }

    @Override
    public PageResponse<DocumentSummaryResponse> echeancier(SensTiers sens, EcheancierFilter f, Pageable pageable) {
        LocalDate avant = Boolean.TRUE.equals(f.enRetard()) ? LocalDate.now() : f.echeanceAvant();
        DocumentFilter filter = new DocumentFilter(f.search(), f.tiersId(), null, null, null, null, null,
                true, avant, null);
        return factures.rechercherFactures(sens, filter, pageable);
    }
}
