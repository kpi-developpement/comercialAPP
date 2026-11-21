package ma.gestcom.modules.stock.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

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
import ma.gestcom.modules.stock.dto.OperationFilter;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.dto.TransfertRequest;
import ma.gestcom.modules.stock.dto.TransfertResponse;
import ma.gestcom.modules.stock.entity.Transfert;
import ma.gestcom.modules.stock.entity.TransfertLigne;
import ma.gestcom.modules.stock.mapper.TransfertMapper;
import ma.gestcom.modules.stock.repository.TransfertRepository;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.StockService;
import ma.gestcom.modules.stock.service.TransfertService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransfertServiceImpl implements TransfertService {

    private final TransfertRepository repository;
    private final TransfertMapper mapper;
    private final DepotService depotService;
    private final StockService stockService;
    private final ArticleService articleService;
    private final NumerotationService numerotationService;

    @Override
    public PageResponse<TransfertResponse> search(OperationFilter filter, Pageable pageable) {
        Specification<Transfert> depot = filter.depotId() == null ? null : (root, q, cb) -> cb.or(
                cb.equal(root.get("depotSourceId"), filter.depotId()),
                cb.equal(root.get("depotDestinationId"), filter.depotId()));
        Specification<Transfert> spec = Specification.allOf(
                SpecificationUtil.containsAny(filter.search(), "numero", "notes"),
                depot,
                SpecificationUtil.equalTo("statut", filter.statut()),
                SpecificationUtil.dateBetween("dateTransfert", filter.dateDebut(), filter.dateFin()));
        Map<Long, String> depots = depotService.getLibelles();
        return PageResponse.from(repository.findAll(spec, pageable), t -> mapper.toResponse(t,
                depots.get(t.getDepotSourceId()), depots.get(t.getDepotDestinationId())));
    }

    @Override
    public TransfertResponse get(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public TransfertResponse create(TransfertRequest request) {
        Transfert transfert = new Transfert();
        apply(transfert, request);
        return toResponse(repository.save(transfert));
    }

    @Override
    @Transactional
    public TransfertResponse update(Long id, TransfertRequest request) {
        Transfert transfert = findBrouillon(id);
        apply(transfert, request);
        return toResponse(transfert);
    }

    @Override
    @Transactional
    public TransfertResponse valider(Long id) {
        Transfert transfert = findBrouillon(id);
        depotService.requireActif(transfert.getDepotSourceId());
        depotService.requireActif(transfert.getDepotDestinationId());
        Map<Long, BigDecimal> besoins = new LinkedHashMap<>();
        transfert.getLignes().forEach(l -> besoins.merge(l.getArticleId(), l.getQuantite(), BigDecimal::add));
        stockService.verifierDisponibilite(transfert.getDepotSourceId(), besoins);

        String numero = numerotationService.nextNumero(TypeDocument.TRANSFERT, transfert.getDateTransfert());
        transfert.setNumero(numero);
        for (TransfertLigne ligne : transfert.getLignes()) {
            stockService.sortie(operation(transfert, ligne, TypeMouvement.TRANSFERT_SORTIE,
                    transfert.getDepotSourceId(), numero));
            stockService.entree(operation(transfert, ligne, TypeMouvement.TRANSFERT_ENTREE,
                    transfert.getDepotDestinationId(), numero));
        }
        transfert.setStatut(StatutOperation.VALIDE);
        return toResponse(transfert);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(findBrouillon(id));
    }

    private StockOperation operation(Transfert t, TransfertLigne ligne, TypeMouvement type, Long depotId,
                                     String numero) {
        return new StockOperation(ligne.getArticleId(), depotId, ligne.getQuantite(), null, type, false,
                TypeDocument.TRANSFERT, t.getId(), numero, "Transfert " + numero);
    }

    private void apply(Transfert transfert, TransfertRequest request) {
        if (request.depotSourceId().equals(request.depotDestinationId())) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                    "Le dépôt de destination doit être différent du dépôt source");
        }
        depotService.requireActif(request.depotSourceId());
        depotService.requireActif(request.depotDestinationId());
        transfert.setDepotSourceId(request.depotSourceId());
        transfert.setDepotDestinationId(request.depotDestinationId());
        transfert.setDateTransfert(request.dateTransfert());
        transfert.setNotes(request.notes());
        transfert.getLignes().clear();
        Map<Long, ArticleInfo> articles = articleService.getInfos(
                request.lignes().stream().map(TransfertRequest.Ligne::articleId).toList());
        for (TransfertRequest.Ligne l : request.lignes()) {
            ArticleInfo article = articles.get(l.articleId());
            if (article == null) {
                throw new NotFoundException("Article", l.articleId());
            }
            if (!article.isStocke()) {
                throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                        "L'article " + article.code() + " n'est pas stocké");
            }
            TransfertLigne ligne = new TransfertLigne();
            ligne.setArticleId(article.id());
            ligne.setArticleCode(article.code());
            ligne.setDesignation(article.designation());
            ligne.setQuantite(MoneyUtil.qty(l.quantite()));
            transfert.addLigne(ligne);
        }
    }

    private TransfertResponse toResponse(Transfert t) {
        Map<Long, String> depots = depotService.getLibelles();
        return mapper.toResponse(t, depots.get(t.getDepotSourceId()), depots.get(t.getDepotDestinationId()));
    }

    private Transfert findBrouillon(Long id) {
        Transfert transfert = find(id);
        if (transfert.getStatut() != StatutOperation.BROUILLON) {
            throw new BusinessException(ErrorCodes.OPERATION_NOT_EDITABLE, "Ce transfert est déjà validé");
        }
        return transfert;
    }

    private Transfert find(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Transfert", id));
    }
}
