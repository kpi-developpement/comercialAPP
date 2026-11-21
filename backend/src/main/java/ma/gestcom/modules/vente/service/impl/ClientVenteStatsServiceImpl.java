package ma.gestcom.modules.vente.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.vente.dto.ClientVenteStats;
import ma.gestcom.modules.vente.repository.ClientVenteStatsRepository;
import ma.gestcom.modules.vente.repository.projection.CaAnnuelRow;
import ma.gestcom.modules.vente.service.ClientVenteStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientVenteStatsServiceImpl implements ClientVenteStatsService {

    private final ClientVenteStatsRepository repository;

    @Override
    public List<ClientVenteStats.ProduitVendu> produitsVendus(Long clientId) {
        return repository.produitsVendus(clientId).stream()
                .map(r -> new ClientVenteStats.ProduitVendu(r.getArticleId(), r.getCode(), r.getDesignation(),
                        MoneyUtil.qty(r.getQuantite()), MoneyUtil.money(r.getMontantHt()),
                        r.getNbFactures() == null ? 0 : r.getNbFactures(), r.getDerniereVente()))
                .toList();
    }

    @Override
    public List<ClientVenteStats.ProduitFacture> facturesProduit(Long clientId, Long articleId) {
        return repository.facturesProduit(clientId, articleId).stream()
                .map(r -> {
                    boolean avoir = "AVOIR".equals(r.getType());
                    return new ClientVenteStats.ProduitFacture(r.getDocumentId(), r.getType(), r.getNumero(),
                            r.getDateDocument(), r.getPrixUnitaireHt(),
                            avoir ? r.getQuantite().negate() : r.getQuantite(),
                            avoir ? r.getMontantHt().negate() : r.getMontantHt());
                })
                .toList();
    }

    @Override
    public List<ClientVenteStats.CaAnnuel> caAnnuel(Long clientId, int anneeDebut, int anneeFin) {
        if (anneeFin < anneeDebut || anneeFin - anneeDebut > 50) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "Intervalle d'années invalide");
        }
        Map<Integer, BigDecimal> parAnnee = repository
                .caAnnuel(clientId, LocalDate.of(anneeDebut, 1, 1), LocalDate.of(anneeFin, 12, 31)).stream()
                .collect(Collectors.toMap(CaAnnuelRow::getAnnee, CaAnnuelRow::getMontant));
        List<ClientVenteStats.CaAnnuel> result = new ArrayList<>();
        for (int a = anneeDebut; a <= anneeFin; a++) {
            result.add(new ClientVenteStats.CaAnnuel(a, MoneyUtil.money(parAnnee.get(a))));
        }
        return result;
    }
}
