package ma.gestcom.modules.vente.service.impl;

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
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.mapper.DocumentVenteMapper;
import ma.gestcom.modules.vente.repository.DocumentVenteRepository;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentVenteServiceImpl implements DocumentVenteService {

    private static final Set<StatutDocument> VALIDES =
            EnumSet.of(StatutDocument.VALIDE, StatutDocument.TRANSFORME_PARTIEL, StatutDocument.TRANSFORME);

    private final DocumentVenteRepository repository;
    private final DocumentVenteMapper mapper;
    private final DocumentVenteHelper helper;
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
        DocumentVente document = new DocumentVente();
        document.setType(type);
        helper.appliquer(document, request);
        linkService.consommer(document, helper.chargeurLignes());
        return helper.toResponse(repository.save(document), List.of());
    }

    @Override
    @Transactional
    public DocumentResponse update(TypeDocument type, Long id, DocumentRequest request) {
        DocumentVente document = helper.findBrouillon(type, id);
        linkService.liberer(document, helper.chargeurLignes());
        helper.appliquer(document, request);
        linkService.consommer(document, helper.chargeurLignes());
        return helper.toResponse(repository.saveAndFlush(document), List.of());
    }

    @Override
    @Transactional
    public void delete(TypeDocument type, Long id) {
        DocumentVente document = helper.findBrouillon(type, id);
        linkService.liberer(document, helper.chargeurLignes());
        repository.delete(document);
    }

    @Override
    public byte[] pdf(TypeDocument type, Long id) {
        return pdfService.generer(get(type, id), "Client");
    }

    @Override
    public FactureInfo getFactureInfo(Long factureId) {
        return mapper.toFactureInfo(findFacture(factureId));
    }

    @Override
    @Transactional
    public void appliquerReglement(Long factureId, BigDecimal delta) {
        DocumentVente facture = findFacture(factureId);
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
    public TiersEncours encours(Long clientId) {
        return new TiersEncours(
                repository.sumTtc(clientId, TypeDocument.FACTURE, VALIDES),
                repository.sumTtc(clientId, TypeDocument.AVOIR, VALIDES),
                repository.sumRegle(clientId, TypeDocument.FACTURE, VALIDES));
    }

    @Override
    public List<DocumentSummaryResponse> facturesNonSoldees(Long clientId) {
        return repository.findAll(DocumentSpecifications.fromFilter(TypeDocument.FACTURE,
                        DocumentFilter.facturesNonSoldees(clientId)), Sort.by("dateEcheance", "dateDocument"))
                .stream().map(mapper::toSummary).toList();
    }

    private DocumentVente findFacture(Long id) {
        return repository.findByIdAndType(id, TypeDocument.FACTURE)
                .orElseThrow(() -> new NotFoundException("Facture", id));
    }
}
