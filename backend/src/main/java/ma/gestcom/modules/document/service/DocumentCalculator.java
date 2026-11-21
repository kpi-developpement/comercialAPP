package ma.gestcom.modules.document.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.document.dto.TvaRecapResponse;
import ma.gestcom.modules.document.entity.AbstractDocument;
import ma.gestcom.modules.document.entity.AbstractDocumentLigne;

/**
 * Totals of commercial documents. Rounding is HALF_UP at scale 2, computed per line then summed:
 * <pre>
 * brut   = qte x PU HT
 * remise = brut x remise%
 * HT     = brut - remise
 * TVA    = HT x taux%
 * TTC    = HT + TVA
 * </pre>
 */
public final class DocumentCalculator {

    private DocumentCalculator() {
    }

    public static void calculerLigne(AbstractDocumentLigne ligne) {
        BigDecimal brut = MoneyUtil.money(MoneyUtil.nz(ligne.getQuantite()).multiply(MoneyUtil.nz(ligne.getPrixUnitaireHt())));
        BigDecimal remise = MoneyUtil.percent(brut, ligne.getRemisePct());
        BigDecimal ht = MoneyUtil.money(brut.subtract(remise));
        BigDecimal tva = MoneyUtil.percent(ht, ligne.getTauxTva());
        ligne.setMontantRemise(remise);
        ligne.setMontantHt(ht);
        ligne.setMontantTva(tva);
        ligne.setMontantTtc(MoneyUtil.money(ht.add(tva)));
    }

    public static void calculerTotaux(AbstractDocument<?> document) {
        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalRemise = BigDecimal.ZERO;
        BigDecimal totalTva = BigDecimal.ZERO;
        for (AbstractDocumentLigne ligne : document.getLignes()) {
            calculerLigne(ligne);
            totalHt = totalHt.add(ligne.getMontantHt());
            totalRemise = totalRemise.add(ligne.getMontantRemise());
            totalTva = totalTva.add(ligne.getMontantTva());
        }
        document.setTotalHt(MoneyUtil.money(totalHt));
        document.setTotalRemise(MoneyUtil.money(totalRemise));
        document.setTotalTva(MoneyUtil.money(totalTva));
        document.setTotalTtc(MoneyUtil.money(totalHt.add(totalTva)));
    }

    /** VAT summary grouped by rate (highest rate first), as printed at the bottom of the document. */
    public static List<TvaRecapResponse> recapTva(Collection<? extends AbstractDocumentLigne> lignes) {
        Map<BigDecimal, BigDecimal[]> parTaux = new TreeMap<>(Comparator.reverseOrder());
        for (AbstractDocumentLigne ligne : lignes) {
            BigDecimal taux = MoneyUtil.money(ligne.getTauxTva());
            BigDecimal[] acc = parTaux.computeIfAbsent(taux, t -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            acc[0] = acc[0].add(MoneyUtil.nz(ligne.getMontantHt()));
            acc[1] = acc[1].add(MoneyUtil.nz(ligne.getMontantTva()));
        }
        return parTaux.entrySet().stream()
                .map(e -> new TvaRecapResponse(e.getKey(), MoneyUtil.money(e.getValue()[0]),
                        MoneyUtil.money(e.getValue()[1])))
                .toList();
    }
}
