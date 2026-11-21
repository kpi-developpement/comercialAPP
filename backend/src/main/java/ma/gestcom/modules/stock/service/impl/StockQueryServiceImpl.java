package ma.gestcom.modules.stock.service.impl;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.stock.dto.EtatStockFilter;
import ma.gestcom.modules.stock.dto.EtatStockResponse;
import ma.gestcom.modules.stock.dto.MouvementFilter;
import ma.gestcom.modules.stock.dto.MouvementResponse;
import ma.gestcom.modules.stock.dto.StockParDepotResponse;
import ma.gestcom.modules.stock.dto.ValorisationResponse;
import ma.gestcom.modules.stock.entity.Depot;
import ma.gestcom.modules.stock.entity.StockArticleDepot;
import ma.gestcom.modules.stock.entity.StockMouvement;
import ma.gestcom.modules.stock.mapper.MouvementMapper;
import ma.gestcom.modules.stock.repository.DepotRepository;
import ma.gestcom.modules.stock.repository.StockArticleDepotRepository;
import ma.gestcom.modules.stock.repository.StockMouvementRepository;
import ma.gestcom.modules.stock.repository.StockReportRepository;
import ma.gestcom.modules.stock.repository.projection.EtatStockRow;
import ma.gestcom.modules.stock.service.StockQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockQueryServiceImpl implements StockQueryService {

    private static final ZoneId ZONE = ZoneId.of("Africa/Casablanca");

    private final StockReportRepository reportRepository;
    private final StockArticleDepotRepository stockRepository;
    private final StockMouvementRepository mouvementRepository;
    private final DepotRepository depotRepository;
    private final ArticleService articleService;
    private final MouvementMapper mouvementMapper;

    @Override
    public PageResponse<EtatStockResponse> etat(EtatStockFilter filter, Pageable pageable) {
        Page<EtatStockRow> page = reportRepository.etatStock(blankToNull(filter.search()), filter.familleId(),
                filter.depotId(), filter.sousMinimum(), pageable);
        return PageResponse.from(page, StockQueryServiceImpl::toEtat);
    }

    @Override
    public PageResponse<EtatStockResponse> alertes(String search, Pageable pageable) {
        return etat(new EtatStockFilter(search, null, null, true), pageable);
    }

    @Override
    public ValorisationResponse valorisation() {
        List<ValorisationResponse.Depot> depots = reportRepository.valorisationParDepot().stream()
                .map(r -> new ValorisationResponse.Depot(r.getDepotId(), r.getDepotCode(), r.getDepotLibelle(),
                        r.getNbArticles() == null ? 0 : r.getNbArticles(),
                        MoneyUtil.qty(r.getQuantite()), MoneyUtil.money(r.getValeur())))
                .toList();
        BigDecimal quantite = depots.stream().map(ValorisationResponse.Depot::quantite)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valeur = depots.stream().map(ValorisationResponse.Depot::valeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ValorisationResponse(depots, MoneyUtil.qty(quantite), MoneyUtil.money(valeur));
    }

    @Override
    public List<StockParDepotResponse> stockParDepot(Long articleId) {
        Map<Long, BigDecimal> quantites = stockRepository.findByArticleId(articleId).stream()
                .collect(Collectors.toMap(StockArticleDepot::getDepotId, StockArticleDepot::getQuantite));
        return depotRepository.findAllByOrderByCodeAsc().stream()
                .map(d -> new StockParDepotResponse(d.getId(), d.getCode(), d.getLibelle(),
                        quantites.getOrDefault(d.getId(), BigDecimal.ZERO)))
                .toList();
    }

    @Override
    public PageResponse<MouvementResponse> mouvements(MouvementFilter filter, Pageable pageable) {
        Specification<StockMouvement> spec = Specification.allOf(
                SpecificationUtil.equalTo("articleId", filter.articleId()),
                SpecificationUtil.equalTo("depotId", filter.depotId()),
                SpecificationUtil.equalTo("type", filter.type()),
                SpecificationUtil.containsAny(filter.search(), "referenceNumero", "libelle"),
                dateBetween(filter));
        Page<StockMouvement> page = mouvementRepository.findAll(spec, pageable);
        Set<Long> articleIds = page.getContent().stream().map(StockMouvement::getArticleId).collect(Collectors.toSet());
        Map<Long, ArticleInfo> articles = articleService.getInfos(articleIds);
        Map<Long, String> depots = depotRepository.findAll().stream()
                .collect(Collectors.toMap(Depot::getId, Depot::getLibelle));
        return PageResponse.from(page, m -> mouvementMapper.toResponse(m, articles.get(m.getArticleId()),
                depots.get(m.getDepotId())));
    }

    private static Specification<StockMouvement> dateBetween(MouvementFilter filter) {
        if (filter.dateDebut() == null && filter.dateFin() == null) {
            return null;
        }
        return (root, query, cb) -> {
            var path = root.<java.time.Instant>get("dateMouvement");
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (filter.dateDebut() != null) {
                predicates.add(cb.greaterThanOrEqualTo(path, filter.dateDebut().atStartOfDay(ZONE).toInstant()));
            }
            if (filter.dateFin() != null) {
                predicates.add(cb.lessThan(path, filter.dateFin().plusDays(1).atStartOfDay(ZONE).toInstant()));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }

    private static EtatStockResponse toEtat(EtatStockRow r) {
        BigDecimal min = MoneyUtil.nz(r.getStockMinimum());
        BigDecimal qte = MoneyUtil.qty(r.getQuantite());
        return new EtatStockResponse(r.getArticleId(), r.getCode(), r.getDesignation(), r.getFamilleLibelle(),
                r.getUnite(), min, qte, MoneyUtil.qty(r.getPump()), MoneyUtil.money(r.getValeur()),
                min.signum() > 0 && qte.compareTo(min) < 0);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
