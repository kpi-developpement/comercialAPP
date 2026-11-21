package ma.gestcom.modules.document.service;

import java.math.BigDecimal;
import java.util.function.Supplier;

import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.document.dto.DocumentLigneRequest;
import ma.gestcom.modules.document.entity.AbstractDocumentLigne;

/**
 * Builds document lines from requests (with article defaults) or by copying a source line (transformation).
 */
public final class DocumentLigneFactory {

    private DocumentLigneFactory() {
    }

    /**
     * @param article       null for a free text line
     * @param remiseDefaut  tiers default discount, applied when the request has no discount
     * @param achat         true to default the price to the purchase price
     */
    public static <L extends AbstractDocumentLigne> L depuisRequete(Supplier<L> factory, DocumentLigneRequest req,
                                                                    ArticleInfo article, BigDecimal remiseDefaut,
                                                                    int ordre, boolean achat) {
        L ligne = factory.get();
        ligne.setOrdre(ordre);
        ligne.setQuantite(MoneyUtil.qty(req.quantite()));
        ligne.setRemisePct(MoneyUtil.money(req.remisePct() != null ? req.remisePct() : remiseDefaut));
        ligne.setSourceLigneId(req.sourceLigneId());
        if (article == null) {
            if (req.designation() == null || req.designation().isBlank()) {
                throw new BusinessException(ErrorCodes.VALIDATION_ERROR,
                        "Ligne " + ordre + " : la désignation est obligatoire pour une ligne libre");
            }
            ligne.setDesignation(req.designation().trim());
            ligne.setPrixUnitaireHt(MoneyUtil.qty(req.prixUnitaireHt()));
            ligne.setTauxTva(MoneyUtil.money(req.tauxTva()));
            ligne.setArticleStocke(false);
            return ligne;
        }
        if (!article.actif()) {
            throw new BusinessException(ErrorCodes.ARTICLE_INACTIVE, "L'article " + article.code() + " est inactif");
        }
        BigDecimal prixDefaut = achat ? article.prixAchatHt() : article.prixVenteHt();
        ligne.setArticleId(article.id());
        ligne.setArticleCode(article.code());
        ligne.setUnite(article.unite());
        ligne.setDesignation(req.designation() == null || req.designation().isBlank()
                ? article.designation() : req.designation().trim());
        ligne.setPrixUnitaireHt(MoneyUtil.qty(req.prixUnitaireHt() != null ? req.prixUnitaireHt() : prixDefaut));
        ligne.setTauxTva(MoneyUtil.money(req.tauxTva() != null ? req.tauxTva() : article.tauxTva()));
        ligne.setArticleStocke(article.isStocke());
        return ligne;
    }

    /** Copies a source line for a transformation; the new line is linked to its source. */
    public static <L extends AbstractDocumentLigne> L copier(Supplier<L> factory, AbstractDocumentLigne source,
                                                             BigDecimal quantite, int ordre) {
        L ligne = factory.get();
        ligne.setOrdre(ordre);
        ligne.setArticleId(source.getArticleId());
        ligne.setArticleCode(source.getArticleCode());
        ligne.setDesignation(source.getDesignation());
        ligne.setUnite(source.getUnite());
        ligne.setQuantite(MoneyUtil.qty(quantite));
        ligne.setPrixUnitaireHt(source.getPrixUnitaireHt());
        ligne.setRemisePct(source.getRemisePct());
        ligne.setTauxTva(source.getTauxTva());
        ligne.setArticleStocke(source.isArticleStocke());
        ligne.setSourceLigneId(source.getId());
        return ligne;
    }
}
