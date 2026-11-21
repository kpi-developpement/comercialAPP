package ma.gestcom.modules.document.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.document.entity.AbstractDocument;
import ma.gestcom.modules.document.entity.AbstractDocumentLigne;
import org.springframework.stereotype.Component;

/**
 * Keeps {@code quantiteRestante} of source lines in sync with the lines that were transformed from them.
 * A draft consumes its source quantities as soon as it exists; editing or deleting the draft releases them.
 */
@Component
public class DocumentLinkService {

    /**
     * Consumes, on the source lines, the quantities of every linked line of {@code cible}.
     *
     * @param chargeur loads a source line by id (throws NotFound when absent)
     */
    public <L extends AbstractDocumentLigne> void consommer(AbstractDocument<L> cible, Function<Long, L> chargeur) {
        Map<Long, AbstractDocument<?>> sourcesTouchees = new LinkedHashMap<>();
        for (L ligne : cible.getLignes()) {
            if (ligne.getSourceLigneId() == null) {
                continue;
            }
            L source = chargeur.apply(ligne.getSourceLigneId());
            AbstractDocument<?> docSource = source.getDocument();
            verifierLien(cible, docSource);
            if (ligne.getQuantite().compareTo(source.getQuantiteRestante()) > 0) {
                throw new BusinessException(ErrorCodes.QUANTITY_EXCEEDS_REMAINING,
                        "Quantité " + ligne.getQuantite().stripTrailingZeros().toPlainString() + " supérieure au reste ("
                                + source.getQuantiteRestante().stripTrailingZeros().toPlainString() + ") pour « "
                                + source.getDesignation() + " » de " + docSource.getNumero());
            }
            source.setQuantiteRestante(MoneyUtil.qty(source.getQuantiteRestante().subtract(ligne.getQuantite())));
            ligne.setSourceDocumentId(docSource.getId());
            sourcesTouchees.put(docSource.getId(), docSource);
        }
        sourcesTouchees.values().forEach(DocumentLinkService::rafraichirStatut);
        cible.setSources(sourcesTouchees.isEmpty() ? null : sourcesTouchees.values().stream()
                .map(AbstractDocument::getNumero)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", ")));
    }

    /** Gives back to the source lines the quantities consumed by {@code cible} (draft edited or deleted). */
    public <L extends AbstractDocumentLigne> void liberer(AbstractDocument<L> cible, Function<Long, L> chargeur) {
        Map<Long, AbstractDocument<?>> sourcesTouchees = new LinkedHashMap<>();
        for (L ligne : cible.getLignes()) {
            if (ligne.getSourceLigneId() == null) {
                continue;
            }
            L source = chargeur.apply(ligne.getSourceLigneId());
            BigDecimal restante = source.getQuantiteRestante().add(ligne.getQuantite()).min(source.getQuantite());
            source.setQuantiteRestante(MoneyUtil.qty(restante));
            sourcesTouchees.put(source.getDocument().getId(), source.getDocument());
        }
        sourcesTouchees.values().forEach(DocumentLinkService::rafraichirStatut);
    }

    /** VALIDE when nothing was transformed, TRANSFORME when everything was, TRANSFORME_PARTIEL in between. */
    public static void rafraichirStatut(AbstractDocument<?> document) {
        if (!document.isValide()) {
            return;
        }
        boolean toutTransforme = document.getLignes().stream().allMatch(l -> l.getQuantiteRestante().signum() == 0);
        boolean rienTransforme = document.getLignes().stream()
                .allMatch(l -> l.getQuantiteRestante().compareTo(l.getQuantite()) == 0);
        if (toutTransforme) {
            document.setStatut(StatutDocument.TRANSFORME);
        } else if (rienTransforme) {
            document.setStatut(StatutDocument.VALIDE);
        } else {
            document.setStatut(StatutDocument.TRANSFORME_PARTIEL);
        }
    }

    private static void verifierLien(AbstractDocument<?> cible, AbstractDocument<?> source) {
        if (!source.getStatut().isTransformable()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Le document " + source.getNumero() + " n'est pas transformable (statut " + source.getStatut() + ")");
        }
        if (!DocumentWorkflow.peutTransformer(source.getType(), cible.getType())) {
            throw new BusinessException(ErrorCodes.TRANSFORMATION_NOT_ALLOWED,
                    source.getType().getLibelle() + " ne peut pas être transformé en " + cible.getType().getLibelle());
        }
        if (!source.getTiersId().equals(cible.getTiersId())) {
            throw new BusinessException(ErrorCodes.GROUPING_INVALID,
                    "Le document " + source.getNumero() + " concerne un autre tiers");
        }
    }
}
