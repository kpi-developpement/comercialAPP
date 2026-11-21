package ma.gestcom.modules.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import ma.gestcom.modules.parametre.dto.SocieteResponse;

/**
 * Legal mentions (ICE, IF, RC, Patente, CNSS, RIB), custom footer text and page number on every page.
 */
class PdfFooter extends PdfPageEventHelper {

    private final String mentions;
    private final String piedDePage;

    PdfFooter(SocieteResponse societe) {
        List<String> parts = new ArrayList<>();
        add(parts, "RC", societe.rc());
        add(parts, "IF", societe.identifiantFiscal());
        add(parts, "ICE", societe.ice());
        add(parts, "Patente", societe.patente());
        add(parts, "CNSS", societe.cnss());
        add(parts, "RIB", societe.rib());
        this.mentions = societe.raisonSociale() + (parts.isEmpty() ? "" : " – " + String.join(" | ", parts));
        this.piedDePage = societe.piedDePage();
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        float center = (document.left() + document.right()) / 2;
        float y = document.bottom() - 20;
        if (piedDePage != null && !piedDePage.isBlank()) {
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(piedDePage, PdfStyles.SMALL_MUTED),
                    center, y, 0);
            y -= 11;
        }
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(mentions, PdfStyles.SMALL_MUTED), center, y, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase("Page " + writer.getPageNumber(), PdfStyles.SMALL_MUTED), document.right(), y - 11, 0);
    }

    private static void add(List<String> parts, String label, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(label + " : " + value);
        }
    }
}
