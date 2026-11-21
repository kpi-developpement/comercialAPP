package ma.gestcom.modules.achat.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.common.util.AmountInWordsUtil;
import ma.gestcom.modules.achat.entity.DocumentAchat;
import ma.gestcom.modules.achat.entity.DocumentAchatLigne;
import ma.gestcom.modules.achat.mapper.DocumentAchatMapper;
import ma.gestcom.modules.achat.repository.DocumentAchatLigneRepository;
import ma.gestcom.modules.achat.repository.DocumentAchatRepository;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.document.dto.DocumentLigneRequest;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.document.service.DocumentLigneFactory;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.service.FournisseurService;
import org.springframework.stereotype.Component;

/**
 * Loading, header/lines assembly and response building shared by the purchase services.
 */
@Component
@RequiredArgsConstructor
class DocumentAchatHelper {

    private final DocumentAchatRepository repository;
    private final DocumentAchatLigneRepository ligneRepository;
    private final DocumentAchatMapper mapper;
    private final FournisseurService fournisseurService;
    private final ArticleService articleService;
    private final DepotService depotService;

    DocumentAchat find(TypeDocument type, Long id) {
        return repository.findByIdAndType(id, type)
                .orElseThrow(() -> new NotFoundException(type.getLibelle(), id));
    }

    DocumentAchat findBrouillon(TypeDocument type, Long id) {
        DocumentAchat document = find(type, id);
        if (!document.getStatut().isEditable()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_NOT_EDITABLE,
                    "Le document " + document.getNumero() + " est validé : il ne peut plus être modifié");
        }
        return document;
    }

    Function<Long, DocumentAchatLigne> chargeurLignes() {
        return id -> ligneRepository.findById(id).orElseThrow(() -> new NotFoundException("Ligne source", id));
    }

    TiersInfo fournisseurActif(Long id) {
        TiersInfo fournisseur = fournisseurService.getInfo(id);
        if (!fournisseur.actif()) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                    "Le fournisseur " + fournisseur.code() + " est inactif");
        }
        return fournisseur;
    }

    void appliquer(DocumentAchat document, DocumentRequest request) {
        TiersInfo fournisseur = fournisseurActif(request.tiersId());
        depotService.requireActif(request.depotId());
        document.setTiersId(fournisseur.id());
        document.setTiersCode(fournisseur.code());
        document.setTiersNom(fournisseur.raisonSociale());
        document.setTiersIce(fournisseur.ice());
        document.setTiersAdresse(fournisseur.adresseComplete());
        document.setDateDocument(request.dateDocument());
        document.setDepotId(request.depotId());
        document.setNotes(request.notes());
        document.setDateEcheance(request.dateEcheance());
        document.setReferenceFournisseur(request.referenceFournisseur());
        document.setRetourMarchandise(DocumentWorkflow.estAvoir(document.getType()) && request.retourMarchandise());

        Map<Long, ArticleInfo> articles = articleService.getInfos(request.lignes().stream()
                .map(DocumentLigneRequest::articleId).filter(Objects::nonNull).toList());
        document.getLignes().clear();
        int ordre = 1;
        for (DocumentLigneRequest ligne : request.lignes()) {
            ArticleInfo article = ligne.articleId() == null ? null : articles.get(ligne.articleId());
            if (ligne.articleId() != null && article == null) {
                throw new NotFoundException("Article", ligne.articleId());
            }
            document.addLigne(DocumentLigneFactory.depuisRequete(DocumentAchatLigne::new, ligne, article,
                    fournisseur.remiseDefaut(), ordre++, true));
        }
        DocumentCalculator.calculerTotaux(document);
    }

    DocumentAchat nouveauDepuis(DocumentAchat source, TypeDocument type, LocalDate date) {
        DocumentAchat cible = new DocumentAchat();
        cible.setType(type);
        cible.setDateDocument(date != null ? date : LocalDate.now());
        cible.setTiersId(source.getTiersId());
        cible.setTiersCode(source.getTiersCode());
        cible.setTiersNom(source.getTiersNom());
        cible.setTiersIce(source.getTiersIce());
        cible.setTiersAdresse(source.getTiersAdresse());
        cible.setDepotId(source.getDepotId());
        return cible;
    }

    DocumentResponse toResponse(DocumentAchat document, List<String> warnings) {
        return mapper.toResponse(document,
                depotService.get(document.getDepotId()).libelle(),
                AmountInWordsUtil.toWords(document.getTotalTtc()),
                DocumentCalculator.recapTva(document.getLignes()),
                DocumentWorkflow.ciblesPossibles(document),
                warnings);
    }
}
