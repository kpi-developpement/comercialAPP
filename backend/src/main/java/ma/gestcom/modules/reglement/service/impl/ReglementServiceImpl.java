package ma.gestcom.modules.reglement.service.impl;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.common.util.SpecificationUtil;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.reglement.dto.AffectationRequest;
import ma.gestcom.modules.reglement.dto.AffectationsRequest;
import ma.gestcom.modules.reglement.dto.ReglementFilter;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.dto.ReglementResponse;
import ma.gestcom.modules.reglement.entity.Reglement;
import ma.gestcom.modules.reglement.entity.ReglementAffectation;
import ma.gestcom.modules.reglement.mapper.ReglementMapper;
import ma.gestcom.modules.reglement.repository.ReglementRepository;
import ma.gestcom.modules.reglement.service.FactureGateway;
import ma.gestcom.modules.reglement.service.ReglementService;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReglementServiceImpl implements ReglementService {

    private final ReglementRepository repository;
    private final ReglementMapper mapper;
    private final FactureGateway factures;
    private final NumerotationService numerotationService;

    @Override
    public PageResponse<ReglementResponse> search(SensTiers sens, ReglementFilter f, Pageable pageable) {
        Specification<Reglement> spec = Specification.allOf(
                SpecificationUtil.equalTo("sens", sens),
                SpecificationUtil.containsAny(f.search(), "numero", "tiersNom", "reference", "banque"),
                SpecificationUtil.equalTo("tiersId", f.tiersId()),
                SpecificationUtil.equalTo("mode", f.mode()),
                SpecificationUtil.equalTo("statut", f.statut()),
                SpecificationUtil.dateBetween("dateReglement", f.dateDebut(), f.dateFin()));
        return PageResponse.from(repository.findAll(spec, pageable), mapper::toResponse);
    }

    @Override
    public ReglementResponse get(SensTiers sens, Long id) {
        return mapper.toResponse(find(sens, id));
    }

    @Override
    @Transactional
    public ReglementResponse create(SensTiers sens, ReglementRequest request) {
        TiersInfo tiers = factures.tiers(sens, request.tiersId());
        if (request.mode().isTitre() && (request.reference() == null || request.reference().isBlank())) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "Le numéro du chèque / de l'effet est obligatoire");
        }
        Reglement reglement = new Reglement();
        reglement.setSens(sens);
        reglement.setTiersId(tiers.id());
        reglement.setTiersNom(tiers.raisonSociale());
        reglement.setDateReglement(request.dateReglement());
        reglement.setMode(request.mode());
        reglement.setMontant(MoneyUtil.money(request.montant()));
        reglement.setReference(request.reference());
        reglement.setBanque(request.banque());
        reglement.setDateEcheance(request.mode().isTitre()
                ? (request.dateEcheance() != null ? request.dateEcheance() : request.dateReglement()) : null);
        reglement.setNotes(request.notes());
        reglement.setStatut(request.mode().isTitre() ? StatutReglement.EN_PORTEFEUILLE : StatutReglement.ENCAISSE);
        reglement.setNumero(numerotationService.nextNumero(
                sens == SensTiers.CLIENT ? TypeDocument.REGLEMENT_CLIENT : TypeDocument.REGLEMENT_FOURNISSEUR,
                request.dateReglement()));
        affecter(reglement, request.affectations() == null ? List.of() : request.affectations());
        return mapper.toResponse(repository.save(reglement));
    }

    @Override
    @Transactional
    public ReglementResponse affecter(SensTiers sens, Long id, AffectationsRequest request) {
        Reglement reglement = find(sens, id);
        if (reglement.getStatut() == StatutReglement.IMPAYE) {
            throw new BusinessException(ErrorCodes.PAYMENT_STATUS_INVALID, "Un règlement impayé ne peut pas être affecté");
        }
        affecter(reglement, request.affectations());
        return mapper.toResponse(reglement);
    }

    @Override
    @Transactional
    public ReglementResponse changerStatut(SensTiers sens, Long id, StatutReglement statut) {
        Reglement reglement = find(sens, id);
        if (!reglement.getStatut().transitionsAutorisees().contains(statut)) {
            throw new BusinessException(ErrorCodes.PAYMENT_STATUS_INVALID,
                    "Passage de " + reglement.getStatut() + " à " + statut + " impossible");
        }
        if (statut == StatutReglement.IMPAYE) {
            reglement.getAffectations().forEach(a -> factures.appliquer(sens, a.getDocumentId(), a.getMontant().negate()));
        }
        reglement.setStatut(statut);
        return mapper.toResponse(reglement);
    }

    @Override
    @Transactional
    public void delete(SensTiers sens, Long id) {
        Reglement reglement = find(sens, id);
        if (reglement.getStatut() != StatutReglement.IMPAYE) {
            reglement.getAffectations().forEach(a -> factures.appliquer(sens, a.getDocumentId(), a.getMontant().negate()));
        }
        repository.delete(reglement);
    }

    /**
     * Allocation rules: same tiers, validated invoice, amount ≤ invoice remaining amount, and the total
     * allocated never exceeds the payment amount.
     */
    private void affecter(Reglement reglement, List<AffectationRequest> demandes) {
        BigDecimal total = demandes.stream().map(AffectationRequest::montant).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (MoneyUtil.money(total).compareTo(reglement.getMontantNonAffecte()) > 0) {
            throw new BusinessException(ErrorCodes.PAYMENT_ALLOCATION_INVALID,
                    "Le total affecté dépasse le montant disponible du règlement");
        }
        for (AffectationRequest demande : demandes) {
            FactureInfo facture = factures.facture(reglement.getSens(), demande.documentId());
            if (!facture.tiersId().equals(reglement.getTiersId())) {
                throw new BusinessException(ErrorCodes.PAYMENT_ALLOCATION_INVALID,
                        "La facture " + facture.numero() + " appartient à un autre tiers");
            }
            BigDecimal montant = MoneyUtil.money(demande.montant());
            if (montant.compareTo(facture.resteAPayer()) > 0) {
                throw new BusinessException(ErrorCodes.PAYMENT_ALLOCATION_INVALID,
                        "Le montant affecté à " + facture.numero() + " dépasse son reste à payer");
            }
            factures.appliquer(reglement.getSens(), facture.id(), montant);
            ReglementAffectation affectation = new ReglementAffectation();
            affectation.setDocumentId(facture.id());
            affectation.setDocumentNumero(facture.numero());
            affectation.setMontant(montant);
            reglement.addAffectation(affectation);
        }
    }

    private Reglement find(SensTiers sens, Long id) {
        return repository.findById(id).filter(r -> r.getSens() == sens)
                .orElseThrow(() -> new NotFoundException("Règlement", id));
    }
}
