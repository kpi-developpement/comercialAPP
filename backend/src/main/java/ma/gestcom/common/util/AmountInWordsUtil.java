package ma.gestcom.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts an amount into French words ("montant en lettres"), e.g.
 * 1 280,50 -> "Mille deux cent quatre-vingts dirhams et cinquante centimes".
 * Follows the traditional French rules (et un, quatre-vingts, cents plural, mille invariable).
 */
public final class AmountInWordsUtil {

    private static final String[] UNITS = {
        "zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
        "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize"
    };
    private static final String[] TENS = {
        "", "dix", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante", "quatre-vingt", "quatre-vingt"
    };

    private AmountInWordsUtil() {
    }

    public static String toWords(BigDecimal amount) {
        return toWords(amount, "dirham", "dirhams", "centime", "centimes");
    }

    public static String toWords(BigDecimal amount, String unitSingular, String unitPlural,
                                 String centSingular, String centPlural) {
        BigDecimal value = MoneyUtil.money(amount);
        boolean negative = value.signum() < 0;
        value = value.abs();
        long integerPart = value.setScale(0, RoundingMode.DOWN).longValueExact();
        int cents = value.subtract(BigDecimal.valueOf(integerPart)).movePointRight(2).intValue();

        StringBuilder sb = new StringBuilder();
        if (negative) {
            sb.append("moins ");
        }
        sb.append(numberToWords(integerPart)).append(' ').append(integerPart > 1 ? unitPlural : unitSingular);
        if (cents > 0) {
            sb.append(" et ").append(numberToWords(cents)).append(' ').append(cents > 1 ? centPlural : centSingular);
        }
        return capitalize(sb.toString());
    }

    public static String numberToWords(long number) {
        if (number == 0) {
            return UNITS[0];
        }
        List<String> parts = new ArrayList<>();
        long milliards = number / 1_000_000_000L;
        long millions = (number / 1_000_000L) % 1000;
        long milliers = (number / 1000L) % 1000;
        int reste = (int) (number % 1000);

        if (milliards > 0) {
            parts.add(below1000((int) milliards, true) + " milliard" + (milliards > 1 ? "s" : ""));
        }
        if (millions > 0) {
            parts.add(below1000((int) millions, true) + " million" + (millions > 1 ? "s" : ""));
        }
        if (milliers > 0) {
            parts.add(milliers == 1 ? "mille" : below1000((int) milliers, false) + " mille");
        }
        if (reste > 0) {
            parts.add(below1000(reste, true));
        }
        return String.join(" ", parts);
    }

    /**
     * @param pluralAllowed false when followed by "mille" (cent / quatre-vingt stay invariable)
     */
    private static String below1000(int n, boolean pluralAllowed) {
        int hundreds = n / 100;
        int rest = n % 100;
        StringBuilder sb = new StringBuilder();
        if (hundreds > 0) {
            if (hundreds > 1) {
                sb.append(UNITS[hundreds]).append(' ');
            }
            sb.append("cent");
            if (hundreds > 1 && rest == 0 && pluralAllowed) {
                sb.append('s');
            }
        }
        if (rest > 0) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(below100(rest, pluralAllowed));
        }
        return sb.toString();
    }

    private static String below100(int n, boolean pluralAllowed) {
        if (n <= 16) {
            return UNITS[n];
        }
        if (n < 20) {
            return "dix-" + UNITS[n - 10];
        }
        int tens = n / 10;
        int unit = n % 10;
        if (tens == 7 || tens == 9) {
            int sub = 10 + unit;
            if (tens == 7 && unit == 1) {
                return "soixante et onze";
            }
            return TENS[tens] + "-" + below100(sub, pluralAllowed);
        }
        if (tens == 8) {
            if (unit == 0) {
                return pluralAllowed ? "quatre-vingts" : "quatre-vingt";
            }
            return "quatre-vingt-" + UNITS[unit];
        }
        if (unit == 0) {
            return TENS[tens];
        }
        if (unit == 1) {
            return TENS[tens] + " et un";
        }
        return TENS[tens] + "-" + UNITS[unit];
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
