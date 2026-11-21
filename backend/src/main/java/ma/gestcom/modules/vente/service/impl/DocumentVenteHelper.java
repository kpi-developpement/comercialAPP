package ma.gestcom.modules.vente.service.impl;

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
import ma.gestcom.modules.tiers.service.ClientService;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import ma.gestcom.modules.vente.mapper.DocumentVenteMapper;
import ma.gestcom.modules.vente.repository.DocumentVenteLigneRepository;
import ma.gestcom.modules.vente.repository.DocumentVenteRepository;
import org.springframework.stereotype.Component;

/**
 * Loading, header/lines assembly and response building shared by the sales services.
 */
@Component
@RequiredArgsConstructor
class DocumentVenteHelper {

    private final DocumentVenteRepository repository;
    private final DocumentVenteLigneRepository ligneRepository;
    private final DocumentVenteMapper mapper;
    private final ClientService clientService;
    private final ArticleService articleService;
    private final DepotService depotService;

    DocumentVente find(TypeDocument type, Long id) {
        return repository.findByIdAndType(id, type)
                .orElseThrow(() -> new NotFoundException(type.getLibelle(), id));
    }

    DocumentVente findBrouillon(TypeDocument type, Long id) {
        DocumentVente document = find(type, id);
        if (!document.getStatut().isEditable()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_NOT_EDITABLE,
                    "Le document " + document.getNumero() + " est validé : il ne peut plus être modifié");
        }
        return document;
    }

    Function<Long, DocumentVenteLigne> chargeurLignes() {
        return id -> ligneRepository.findById(id).orElseThrow(() -> new NotFoundException("Ligne source", id));
    }

    /**
     * Client allowed to receive a document of {@code type}: active, not blocked, and a prospect only for devis.
     */
    TiersInfo clientActif(Long clientId, TypeDocument type) {
        TiersInfo client = clientService.getInfo(clientId);
        if (!client.actif()) {
            throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "Le client " + client.code() + " est inactif");
        }
        if (client.bloque() && type != TypeDocument.AVOIR) {
            throw new BusinessException(ErrorCodes.CLIENT_BLOQUE,
                    "Le compte du client " + client.code() + " est bloqué : aucun nouveau document de vente");
        }
        if (client.prospect() && type != TypeDocument.DEVIS) {
            throw new BusinessException(ErrorCodes.CLIENT_PROSPECT,
                    client.raisonSociale() + " est un prospect : seuls les devis sont autorisés. Passez-le en client.");
        }
        return client;
    }

    /** Applies header and lines of a draft request, then recomputes totals (backend is the source of truth). */
    void appliquer(DocumentVente document, DocumentRequest request) {
        TiersInfo client = clientActif(request.tiersId(), document.getType());
        depotService.requireActif(request.depotId());
        snapshotTiers(document, client);
        document.setDateDocument(request.dateDocument());
        document.setDepotId(request.depotId());
        document.setNotes(request.notes());
        document.setDateEcheance(request.dateEcheance());
        document.setDateValidite(document.getType() == TypeDocument.DEVIS ? request.dateValidite() : null);
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
            document.addLigne(DocumentLigneFactory.depuisRequete(DocumentVenteLigne::new, ligne, article,
                    client.remiseDefaut(), ordre++, false));
        }
        DocumentCalculator.calculerTotaux(document);
    }

    /** New draft of {@code type} for the same client/depot as {@code source}. */
    DocumentVente nouveauDepuis(DocumentVente source, TypeDocument type, LocalDate date) {
        DocumentVente cible = new DocumentVente();
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

    DocumentResponse toResponse(DocumentVente document, List<String> warnings) {
        return mapper.toResponse(document,
                depotService.get(document.getDepotId()).libelle(),
                AmountInWordsUtil.toWords(document.getTotalTtc()),
                DocumentCalculator.recapTva(document.getLignes()),
                DocumentWorkflow.ciblesPossibles(document),
                warnings);
    }

    private static void snapshotTiers(DocumentVente document, TiersInfo client) {
        document.setTiersId(client.id());
        document.setTiersCode(client.code());
        document.setTiersNom(client.raisonSociale());
        document.setTiersIce(client.ice());
        document.setTiersAdresse(client.adresseComplete());
    }
}
