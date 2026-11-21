package ma.gestcom.modules.stock.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeMouvement;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.parametre.service.SocieteService;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.entity.StockArticleDepot;
import ma.gestcom.modules.stock.entity.StockMouvement;
import ma.gestcom.modules.stock.repository.StockArticleDepotRepository;
import ma.gestcom.modules.stock.repository.StockMouvementRepository;
import ma.gestcom.modules.stock.service.PumpCalculator;
import ma.gestcom.modules.stock.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockArticleDepotRepository stockRepository;
    private final StockMouvementRepository mouvementRepository;
    private final ArticleService articleService;
    private final SocieteService societeService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void entree(StockOperation op) {
        ArticleInfo article = articleService.getInfo(op.articleId());
        if (!article.isStocke() || !MoneyUtil.isPositive(op.quantite())) {
            return;
        }
        StockArticleDepot stock = lockStock(op.articleId(), op.depotId());
        BigDecimal pump = article.pump();
        if (op.recalculerPump()) {
            BigDecimal stockTotalAvant = stockRepository.sumQuantite(op.articleId());
            pump = PumpCalculator.calculer(stockTotalAvant, article.pump(), op.quantite(), op.coutUnitaire());
            articleService.updatePump(op.articleId(), pump);
        }
        stock.setQuantite(MoneyUtil.qty(stock.getQuantite().add(op.quantite())));
        BigDecimal cout = op.coutUnitaire() != null ? op.coutUnitaire() : article.pump();
        journaliser(op, op.quantite(), cout, stock.getQuantite(), pump);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void sortie(StockOperation op) {
        ArticleInfo article = articleService.getInfo(op.articleId());
        if (!article.isStocke() || !MoneyUtil.isPositive(op.quantite())) {
            return;
        }
        StockArticleDepot stock = lockStock(op.articleId(), op.depotId());
        if (stock.getQuantite().compareTo(op.quantite()) < 0 && !societeService.isStockNegatifAutorise()) {
            throw insuffisant(article, stock.getQuantite(), op.quantite());
        }
        stock.setQuantite(MoneyUtil.qty(stock.getQuantite().subtract(op.quantite())));
        journaliser(op, op.quantite().negate(), article.pump(), stock.getQuantite(), article.pump());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void ajuster(StockOperation op, BigDecimal ecartSigne) {
        ArticleInfo article = articleService.getInfo(op.articleId());
        if (!article.isStocke() || ecartSigne == null || ecartSigne.signum() == 0) {
            return;
        }
        StockArticleDepot stock = lockStock(op.articleId(), op.depotId());
        stock.setQuantite(MoneyUtil.qty(stock.getQuantite().add(ecartSigne)));
        journaliser(op, ecartSigne, article.pump(), stock.getQuantite(), article.pump());
    }

    @Override
    public void verifierDisponibilite(Long depotId, Map<Long, BigDecimal> besoins) {
        if (besoins.isEmpty() || societeService.isStockNegatifAutorise()) {
            return;
        }
        Map<Long, ArticleInfo> articles = articleService.getInfos(besoins.keySet());
        Map<Long, BigDecimal> disponibles = getQuantites(depotId, besoins.keySet());
        List<String> manquants = new ArrayList<>();
        besoins.forEach((articleId, besoin) -> {
            ArticleInfo article = articles.get(articleId);
            if (article == null || !article.isStocke()) {
                return;
            }
            BigDecimal dispo = disponibles.getOrDefault(articleId, BigDecimal.ZERO);
            if (dispo.compareTo(besoin) < 0) {
                manquants.add(article.code() + " (disponible " + dispo.stripTrailingZeros().toPlainString()
                        + ", demandé " + besoin.stripTrailingZeros().toPlainString() + ")");
            }
        });
        if (!manquants.isEmpty()) {
            throw new BusinessException(ErrorCodes.STOCK_INSUFFICIENT,
                    "Stock insuffisant : " + String.join(", ", manquants));
        }
    }

    @Override
    public BigDecimal getQuantite(Long articleId, Long depotId) {
        return stockRepository.findByArticleIdAndDepotId(articleId, depotId)
                .map(StockArticleDepot::getQuantite)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public Map<Long, BigDecimal> getQuantites(Long depotId, Collection<Long> articleIds) {
        if (articleIds.isEmpty()) {
            return Map.of();
        }
        return stockRepository.findByDepotIdAndArticleIdIn(depotId, articleIds).stream()
                .collect(Collectors.toMap(StockArticleDepot::getArticleId, StockArticleDepot::getQuantite));
    }

    private StockArticleDepot lockStock(Long articleId, Long depotId) {
        return stockRepository.findForUpdate(articleId, depotId).orElseGet(() -> {
            stockRepository.insertIfAbsent(articleId, depotId);
            return stockRepository.findForUpdate(articleId, depotId).orElseThrow();
        });
    }

    private void journaliser(StockOperation op, BigDecimal quantiteSignee, BigDecimal cout,
                             BigDecimal stockApres, BigDecimal pumpApres) {
        StockMouvement m = new StockMouvement();
        m.setDateMouvement(Instant.now());
        m.setArticleId(op.articleId());
        m.setDepotId(op.depotId());
        m.setType(op.type());
        m.setQuantite(MoneyUtil.qty(quantiteSignee));
        m.setCoutUnitaire(MoneyUtil.qty(cout));
        m.setStockApres(stockApres);
        m.setPumpApres(MoneyUtil.qty(pumpApres));
        m.setReferenceType(op.referenceType());
        m.setReferenceId(op.referenceId());
        m.setReferenceNumero(op.referenceNumero());
        m.setLibelle(op.libelle() != null ? op.libelle() : defaultLibelle(op.type()));
        mouvementRepository.save(m);
    }

    private static String defaultLibelle(TypeMouvement type) {
        return switch (type) {
            case ENTREE_ACHAT -> "Entrée achat";
            case SORTIE_VENTE -> "Sortie vente";
            case RETOUR_CLIENT -> "Retour client";
            case RETOUR_FOURNISSEUR -> "Retour fournisseur";
            case TRANSFERT_ENTREE -> "Transfert entrant";
            case TRANSFERT_SORTIE -> "Transfert sortant";
            case AJUSTEMENT_INVENTAIRE -> "Ajustement d'inventaire";
        };
    }

    private static BusinessException insuffisant(ArticleInfo article, BigDecimal dispo, BigDecimal demande) {
        return new BusinessException(ErrorCodes.STOCK_INSUFFICIENT,
                "Stock insuffisant pour " + article.code() + " – " + article.designation()
                        + " : disponible " + dispo.stripTrailingZeros().toPlainString()
                        + ", demandé " + demande.stripTrailingZeros().toPlainString());
    }
}
