package ma.gestcom.modules.stock.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.StatutOperation;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.stock.dto.InventaireComptageRequest;
import ma.gestcom.modules.stock.dto.InventaireCreateRequest;
import ma.gestcom.modules.stock.dto.InventaireResponse;
import ma.gestcom.modules.stock.dto.InventaireSummaryResponse;
import ma.gestcom.modules.stock.dto.OperationFilter;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.entity.Inventaire;
import ma.gestcom.modules.stock.entity.InventaireLigne;
import ma.gestcom.modules.stock.mapper.InventaireMapper;
import ma.gestcom.modules.stock.repository.InventaireRepository;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.InventaireService;
import ma.gestcom.modules.stock.service.StockService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventaireServiceImpl implements InventaireService {

    private final InventaireRepository repository;
    private final InventaireMapper mapper;
    private final DepotService depotService;
    private final StockService stockService;
    private final ArticleService articleService;
    private final NumerotationService numerotationService;

    @Override
    public PageResponse<InventaireSummaryResponse> search(OperationFilter filter, Pageable pageable) {
        Specification<Inventaire> spec = Specification.allOf(
                SpecificationUtil.containsAny(filter.search(), "numero", "notes"),
                SpecificationUtil.equalTo("depotId", filter.depotId()),
                SpecificationUtil.equalTo("statut", filter.statut()),
                SpecificationUtil.dateBetween("dateInventaire", filter.dateDebut(), filter.dateFin()));
        Map<Long, String> depots = depotService.getLibelles();
        return PageResponse.from(repository.findAll(spec, pageable),
                i -> mapper.toSummary(i, depots.get(i.getDepotId())));
    }

    @Override
    public InventaireResponse get(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public InventaireResponse create(InventaireCreateRequest request) {
        depotService.requireActif(request.depotId());
        List<ArticleInfo> articles = articleService.findStockables(request.familleId());
        if (articles.isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Aucun article stocké à inventorier");
        }
        Map<Long, BigDecimal> quantites = stockService.getQuantites(request.depotId(),
                articles.stream().map(ArticleInfo::id).toList());
        Inventaire inventaire = new Inventaire();
        inventaire.setDepotId(request.depotId());
        inventaire.setDateInventaire(request.dateInventaire());
        inventaire.setNotes(request.notes());
        for (ArticleInfo article : articles) {
            InventaireLigne ligne = new InventaireLigne();
            ligne.setArticleId(article.id());
            ligne.setArticleCode(article.code());
            ligne.setDesignation(article.designation());
            ligne.setQuantiteTheorique(quantites.getOrDefault(article.id(), BigDecimal.ZERO));
            inventaire.addLigne(ligne);
        }
        return toResponse(repository.save(inventaire));
    }

    @Override
    @Transactional
    public InventaireResponse saisirComptage(Long id, InventaireComptageRequest request) {
        Inventaire inventaire = findBrouillon(id);
        Map<Long, InventaireLigne> lignes = inventaire.getLignes().stream()
                .collect(Collectors.toMap(InventaireLigne::getId, Function.identity()));
        for (InventaireComptageRequest.Ligne saisie : request.lignes()) {
            InventaireLigne ligne = lignes.get(saisie.ligneId());
            if (ligne == null) {
                throw new NotFoundException("Ligne d'inventaire", saisie.ligneId());
            }
            ligne.setQuantiteComptee(saisie.quantiteComptee() == null ? null : MoneyUtil.qty(saisie.quantiteComptee()));
        }
        inventaire.setNotes(request.notes());
        return toResponse(inventaire);
    }

    @Override
    @Transactional
    public InventaireResponse valider(Long id) {
        Inventaire inventaire = findBrouillon(id);
        if (inventaire.getLignes().stream().allMatch(l -> l.getQuantiteComptee() == null)) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Aucune quantité comptée n'a été saisie");
        }
        String numero = numerotationService.nextNumero(TypeDocument.INVENTAIRE, inventaire.getDateInventaire());
        inventaire.setNumero(numero);
        for (InventaireLigne ligne : inventaire.getLignes()) {
            if (ligne.getQuantiteComptee() == null) {
                continue;
            }
            // Gap is computed against the real stock at validation time (movements may have happened meanwhile)
            ligne.setQuantiteTheorique(stockService.getQuantite(ligne.getArticleId(), inventaire.getDepotId()));
            BigDecimal ecart = ligne.getEcart();
            stockService.ajuster(new StockOperation(ligne.getArticleId(), inventaire.getDepotId(), ecart.abs(), null,
                    TypeMouvement.AJUSTEMENT_INVENTAIRE, false, TypeDocument.INVENTAIRE, inventaire.getId(), numero,
                    "Inventaire " + numero), ecart);
        }
        inventaire.setStatut(StatutOperation.VALIDE);
        return toResponse(inventaire);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(findBrouillon(id));
    }

    private InventaireResponse toResponse(Inventaire inventaire) {
        return mapper.toResponse(inventaire, depotService.get(inventaire.getDepotId()).libelle());
    }

    private Inventaire findBrouillon(Long id) {
        Inventaire inventaire = find(id);
        if (inventaire.getStatut() != StatutOperation.BROUILLON) {
            throw new BusinessException(ErrorCodes.OPERATION_NOT_EDITABLE, "Cet inventaire est déjà validé");
        }
        return inventaire;
    }

    private Inventaire find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Inventaire", id));
    }
}
