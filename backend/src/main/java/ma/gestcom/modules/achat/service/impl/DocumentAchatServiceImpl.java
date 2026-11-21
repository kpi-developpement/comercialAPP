package ma.gestcom.modules.achat.service.impl;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.achat.entity.DocumentAchat;
import ma.gestcom.modules.achat.mapper.DocumentAchatMapper;
import ma.gestcom.modules.achat.repository.DocumentAchatRepository;
import ma.gestcom.modules.achat.service.DocumentAchatService;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.document.dto.TiersEncours;
import ma.gestcom.modules.document.service.DocumentLinkService;
import ma.gestcom.modules.document.service.DocumentPdfService;
import ma.gestcom.modules.document.service.DocumentSpecifications;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentAchatServiceImpl implements DocumentAchatService {

    private static final Set<StatutDocument> VALIDES =
            EnumSet.of(StatutDocument.VALIDE, StatutDocument.TRANSFORME_PARTIEL, StatutDocument.TRANSFORME);

    private final DocumentAchatRepository repository;
    private final DocumentAchatMapper mapper;
    private final DocumentAchatHelper helper;
    private final DocumentLinkService linkService;
    private final DocumentPdfService pdfService;

    @Override
    public PageResponse<DocumentSummaryResponse> search(TypeDocument type, DocumentFilter filter, Pageable pageable) {
        return PageResponse.from(repository.findAll(DocumentSpecifications.fromFilter(type, filter), pageable),
                mapper::toSummary);
    }

    @Override
    public DocumentResponse get(TypeDocument type, Long id) {
        return helper.toResponse(helper.find(type, id), List.of());
    }

    @Override
    @Transactional
    public DocumentResponse create(TypeDocument type, DocumentRequest request) {
        DocumentAchat document = new DocumentAchat();
        document.setType(type);
        helper.appliquer(document, request);
        linkService.consommer(document, helper.chargeurLignes());
        return helper.toResponse(repository.save(document), List.of());
    }

    @Override
    @Transactional
    public DocumentResponse update(TypeDocument type, Long id, DocumentRequest request) {
        DocumentAchat document = helper.findBrouillon(type, id);
        linkService.liberer(document, helper.chargeurLignes());
        helper.appliquer(document, request);
        linkService.consommer(document, helper.chargeurLignes());
        return helper.toResponse(repository.saveAndFlush(document), List.of());
    }

    @Override
    @Transactional
    public void delete(TypeDocument type, Long id) {
        DocumentAchat document = helper.findBrouillon(type, id);
        linkService.liberer(document, helper.chargeurLignes());
        repository.delete(document);
    }

    @Override
    public byte[] pdf(TypeDocument type, Long id) {
        return pdfService.generer(get(type, id), "Fournisseur");
    }

    @Override
    public FactureInfo getFactureInfo(Long factureId) {
        return mapper.toFactureInfo(findFacture(factureId));
    }

    @Override
    @Transactional
    public void appliquerReglement(Long factureId, BigDecimal delta) {
        DocumentAchat facture = findFacture(factureId);
        if (!facture.isValide()) {
            throw new BusinessException(ErrorCodes.PAYMENT_ALLOCATION_INVALID,
                    "La facture " + facture.getNumero() + " n'est pas validée");
        }
        BigDecimal nouveau = MoneyUtil.money(facture.getMontantRegle().add(delta));
        if (nouveau.signum() < 0 || nouveau.compareTo(facture.getTotalTtc()) > 0) {
            throw new BusinessException(ErrorCodes.PAYMENT_ALLOCATION_INVALID,
                    "Le montant affecté dépasse le reste à payer de la facture " + facture.getNumero());
        }
        facture.setMontantRegle(nouveau);
        facture.setStatutPaiement(DocumentWorkflow.statutPaiement(nouveau, facture.getTotalTtc()));
    }

    @Override
    public TiersEncours encours(Long fournisseurId) {
        return new TiersEncours(
                repository.sumTtc(fournisseurId, TypeDocument.FACTURE_FOURNISSEUR, VALIDES),
                repository.sumTtc(fournisseurId, TypeDocument.AVOIR_FOURNISSEUR, VALIDES),
                repository.sumRegle(fournisseurId, TypeDocument.FACTURE_FOURNISSEUR, VALIDES));
    }

    @Override
    public List<DocumentSummaryResponse> facturesNonSoldees(Long fournisseurId) {
        return repository.findAll(DocumentSpecifications.fromFilter(TypeDocument.FACTURE_FOURNISSEUR,
                        DocumentFilter.facturesNonSoldees(fournisseurId)), Sort.by("dateEcheance", "dateDocument"))
                .stream().map(mapper::toSummary).toList();
    }

    private DocumentAchat findFacture(Long id) {
        return repository.findByIdAndType(id, TypeDocument.FACTURE_FOURNISSEUR)
                .orElseThrow(() -> new NotFoundException("Facture fournisseur", id));
    }
}
