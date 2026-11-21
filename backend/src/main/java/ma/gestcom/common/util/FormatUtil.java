package ma.gestcom.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * French display formats used in printed documents: "1 234,56" and "21/11/2026".
 */
public final class FormatUtil {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private FormatUtil() {
    }

    public static String montant(BigDecimal value) {
        return decimal("#,##0.00").format(MoneyUtil.money(value));
    }

    /** Quantity without useless trailing zeros: 10 / 2,5 / 0,125. */
    public static String quantite(BigDecimal value) {
        return decimal("#,##0.###").format(MoneyUtil.nz(value));
    }

    public static String date(LocalDate date) {
        return date == null ? "" : DATE.format(date);
    }

    private static DecimalFormat decimal(String pattern) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat(pattern, symbols);
    }
}
