package ma.gestcom.modules.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.gestcom.common.util.FormatUtil;
import ma.gestcom.modules.document.dto.DocumentLigneResponse;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.TvaRecapResponse;
import ma.gestcom.modules.document.service.DocumentPdfService;
import ma.gestcom.modules.parametre.dto.SocieteResponse;
import ma.gestcom.modules.parametre.service.SocieteService;
import org.springframework.stereotype.Service;

/**
 * OpenPDF implementation: company header, tiers block, lines, VAT summary per rate, totals,
 * amount in words and legal footer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPdfServiceImpl implements DocumentPdfService {

    private final SocieteService societeService;

    @Override
    public byte[] generer(DocumentResponse doc, String tiersLabel) {
        SocieteResponse societe = societeService.get();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document pdf = new Document(PageSize.A4, 36, 36, 36, 72);
        try {
            PdfWriter writer = PdfWriter.getInstance(pdf, out);
            writer.setPageEvent(new PdfFooter(societe));
            pdf.open();
            pdf.add(entete(doc, societe));
            pdf.add(blocTiers(doc, tiersLabel));
            pdf.add(tableLignes(doc));
            pdf.add(blocTotaux(doc, societe));
            Paragraph lettres = new Paragraph("Arrêté le présent document à la somme de : " + doc.montantEnLettres(),
                    PdfStyles.BOLD);
            lettres.setSpacingBefore(12);
            pdf.add(lettres);
            if (doc.notes() != null && !doc.notes().isBlank()) {
                Paragraph notes = new Paragraph("Notes : " + doc.notes(), PdfStyles.SMALL);
                notes.setSpacingBefore(8);
                pdf.add(notes);
            }
        } catch (DocumentException e) {
            throw new IllegalStateException("Génération PDF impossible", e);
        } finally {
            pdf.close();
        }
        return out.toByteArray();
    }

    private PdfPTable entete(DocumentResponse doc, SocieteResponse societe) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{60, 40});
        table.setWidthPercentage(100);

        PdfPCell gauche = PdfStyles.cellSansBordure();
        Image logo = logo(societe.logo());
        if (logo != null) {
            logo.scaleToFit(120, 60);
            gauche.addElement(logo);
        }
        gauche.addElement(new Paragraph(societe.raisonSociale(), PdfStyles.TITLE));
        addIfPresent(gauche, societe.entete());
        addIfPresent(gauche, societe.adresse());
        addIfPresent(gauche, societe.ville());
        addIfPresent(gauche, join("Tél : ", societe.telephone(), "  ", "Email : ", societe.email()));
        table.addCell(gauche);

        PdfPCell droite = PdfStyles.cellSansBordure();
        droite.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph titre = new Paragraph(doc.type().getLibelle().toUpperCase(), PdfStyles.HEADING);
        titre.setAlignment(Element.ALIGN_RIGHT);
        droite.addElement(titre);
        droite.addElement(rightLine("N° : " + (doc.numero() != null ? doc.numero() : "BROUILLON")));
        droite.addElement(rightLine("Date : " + FormatUtil.date(doc.dateDocument())));
        if (doc.dateEcheance() != null) {
            droite.addElement(rightLine("Échéance : " + FormatUtil.date(doc.dateEcheance())));
        }
        if (doc.dateValidite() != null) {
            droite.addElement(rightLine("Valable jusqu'au : " + FormatUtil.date(doc.dateValidite())));
        }
        if (doc.referenceFournisseur() != null && !doc.referenceFournisseur().isBlank()) {
            droite.addElement(rightLine("Réf. fournisseur : " + doc.referenceFournisseur()));
        }
        if (doc.sources() != null) {
            droite.addElement(rightLine("Origine : " + doc.sources()));
        }
        table.addCell(droite);
        table.setSpacingAfter(14);
        return table;
    }

    private PdfPTable blocTiers(DocumentResponse doc, String tiersLabel) {
        PdfPTable table = new PdfPTable(new float[]{50, 50});
        table.setWidthPercentage(100);
        table.addCell(PdfStyles.cellSansBordure());
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBorderColor(PdfStyles.BORDER);
        cell.addElement(new Paragraph(tiersLabel, PdfStyles.SMALL_MUTED));
        cell.addElement(new Paragraph(doc.tiersNom(), PdfStyles.BOLD));
        addIfPresent(cell, doc.tiersAdresse());
        addIfPresent(cell, doc.tiersIce() == null ? null : "ICE : " + doc.tiersIce());
        table.addCell(cell);
        table.setSpacingAfter(14);
        return table;
    }

    private PdfPTable tableLignes(DocumentResponse doc) {
        PdfPTable table = new PdfPTable(new float[]{12, 38, 9, 12, 8, 8, 13});
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        for (String h : new String[]{"Code", "Désignation", "Qté", "PU HT", "Rem. %", "TVA %", "Montant HT"}) {
            table.addCell(PdfStyles.headerCell(h));
        }
        for (DocumentLigneResponse l : doc.lignes()) {
            table.addCell(PdfStyles.cell(l.articleCode() == null ? "" : l.articleCode(), Element.ALIGN_LEFT));
            table.addCell(PdfStyles.cell(l.designation(), Element.ALIGN_LEFT));
            table.addCell(PdfStyles.cell(FormatUtil.quantite(l.quantite()), Element.ALIGN_RIGHT));
            table.addCell(PdfStyles.cell(FormatUtil.montant(l.prixUnitaireHt()), Element.ALIGN_RIGHT));
            table.addCell(PdfStyles.cell(FormatUtil.quantite(l.remisePct()), Element.ALIGN_RIGHT));
            table.addCell(PdfStyles.cell(FormatUtil.quantite(l.tauxTva()), Element.ALIGN_RIGHT));
            table.addCell(PdfStyles.cell(FormatUtil.montant(l.montantHt()), Element.ALIGN_RIGHT));
        }
        table.setSpacingAfter(12);
        return table;
    }

    private PdfPTable blocTotaux(DocumentResponse doc, SocieteResponse societe) {
        PdfPTable conteneur = new PdfPTable(new float[]{55, 5, 40});
        conteneur.setWidthPercentage(100);

        PdfPTable tva = new PdfPTable(new float[]{30, 35, 35});
        tva.setWidthPercentage(100);
        tva.addCell(PdfStyles.headerCell("Taux TVA"));
        tva.addCell(PdfStyles.headerCell("Base HT"));
        tva.addCell(PdfStyles.headerCell("Montant TVA"));
        for (TvaRecapResponse r : doc.recapTva()) {
            tva.addCell(PdfStyles.cell(FormatUtil.quantite(r.taux()) + " %", Element.ALIGN_RIGHT));
            tva.addCell(PdfStyles.cell(FormatUtil.montant(r.baseHt()), Element.ALIGN_RIGHT));
            tva.addCell(PdfStyles.cell(FormatUtil.montant(r.montantTva()), Element.ALIGN_RIGHT));
        }
        PdfPCell gauche = PdfStyles.cellSansBordure();
        gauche.addElement(tva);
        conteneur.addCell(gauche);
        conteneur.addCell(PdfStyles.cellSansBordure());

        String devise = societe.devise();
        PdfPTable totaux = new PdfPTable(new float[]{55, 45});
        totaux.setWidthPercentage(100);
        ligneTotal(totaux, "Total remise", FormatUtil.montant(doc.totalRemise()), false);
        ligneTotal(totaux, "Total HT", FormatUtil.montant(doc.totalHt()), false);
        ligneTotal(totaux, "Total TVA", FormatUtil.montant(doc.totalTva()), false);
        ligneTotal(totaux, "Total TTC (" + devise + ")", FormatUtil.montant(doc.totalTtc()), true);
        PdfPCell droite = PdfStyles.cellSansBordure();
        droite.addElement(totaux);
        conteneur.addCell(droite);
        return conteneur;
    }

    private static void ligneTotal(PdfPTable table, String label, String valeur, boolean fort) {
        PdfPCell l = PdfStyles.cell(label, Element.ALIGN_LEFT);
        PdfPCell v = PdfStyles.cell(valeur, Element.ALIGN_RIGHT);
        if (fort) {
            l.setPhrase(new Phrase(label, PdfStyles.BOLD_WHITE));
            v.setPhrase(new Phrase(valeur, PdfStyles.BOLD_WHITE));
            l.setBackgroundColor(PdfStyles.PRIMARY);
            v.setBackgroundColor(PdfStyles.PRIMARY);
        }
        table.addCell(l);
        table.addCell(v);
    }

    private static Paragraph rightLine(String text) {
        Paragraph p = new Paragraph(text, PdfStyles.NORMAL);
        p.setAlignment(Element.ALIGN_RIGHT);
        return p;
    }

    private static void addIfPresent(PdfPCell cell, String text) {
        if (text != null && !text.isBlank()) {
            cell.addElement(new Paragraph(text, PdfStyles.NORMAL));
        }
    }

    private static String join(String l1, String v1, String sep, String l2, String v2) {
        boolean has1 = v1 != null && !v1.isBlank();
        boolean has2 = v2 != null && !v2.isBlank();
        return (has1 ? l1 + v1 : "") + (has1 && has2 ? sep : "") + (has2 ? l2 + v2 : "");
    }

    private static Image logo(String dataUrl) {
        if (dataUrl == null || !dataUrl.startsWith("data:image")) {
            return null;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(dataUrl.substring(dataUrl.indexOf(',') + 1));
            return Image.getInstance(bytes);
        } catch (Exception e) {
            log.warn("Logo société illisible, ignoré : {}", e.getMessage());
            return null;
        }
    }
}
