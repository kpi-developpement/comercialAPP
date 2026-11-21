package ma.gestcom.modules.document.service.impl;

import java.awt.Color;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;

/**
 * Fonts, colors and cell factories of printed documents (CP1252 so French accents render with Helvetica).
 */
final class PdfStyles {

    static final Color PRIMARY = new Color(30, 64, 120);
    static final Color HEADER_BG = new Color(232, 238, 247);
    static final Color BORDER = new Color(200, 206, 216);
    static final Color MUTED = new Color(110, 117, 130);

    static final Font NORMAL = font(9, Font.NORMAL, Color.BLACK);
    static final Font SMALL = font(8, Font.NORMAL, Color.BLACK);
    static final Font SMALL_MUTED = font(8, Font.NORMAL, MUTED);
    static final Font BOLD = font(9, Font.BOLD, Color.BLACK);
    static final Font BOLD_WHITE = font(10, Font.BOLD, Color.WHITE);
    static final Font HEADER = font(8, Font.BOLD, PRIMARY);
    static final Font TITLE = font(13, Font.BOLD, PRIMARY);
    static final Font HEADING = font(16, Font.BOLD, PRIMARY);

    private PdfStyles() {
    }

    static PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER));
        cell.setBackgroundColor(HEADER_BG);
        cell.setBorderColor(BORDER);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    static PdfPCell cell(String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL));
        cell.setBorderColor(BORDER);
        cell.setPadding(4);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    static PdfPCell cellSansBordure() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0);
        return cell;
    }

    private static Font font(float size, int style, Color color) {
        return FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED, size, style, color);
    }
}
