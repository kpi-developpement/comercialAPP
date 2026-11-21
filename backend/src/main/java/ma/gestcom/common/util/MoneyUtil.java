package ma.gestcom.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Rounding conventions: money is scale 2, quantities and unit prices are scale 3, always HALF_UP.
 */
public final class MoneyUtil {

    public static final int MONEY_SCALE = 2;
    public static final int QTY_SCALE = 3;
    public static final BigDecimal HUNDRED = new BigDecimal("100");

    private MoneyUtil() {
    }

    public static BigDecimal money(BigDecimal value) {
        return nz(value).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal qty(BigDecimal value) {
        return nz(value).setScale(QTY_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /** {@code base * pct / 100}, rounded to money scale. */
    public static BigDecimal percent(BigDecimal base, BigDecimal pct) {
        return money(nz(base).multiply(nz(pct)).divide(HUNDRED, 6, RoundingMode.HALF_UP));
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }
}
