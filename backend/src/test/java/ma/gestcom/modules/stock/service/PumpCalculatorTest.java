package ma.gestcom.modules.stock.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PumpCalculatorTest {

    @Test
    void moyennePondereeDesEntrees() {
        // (10 x 100 + 30 x 120) / 40 = 115
        assertThat(PumpCalculator.calculer(bd("10"), bd("100"), bd("30"), bd("120"))).isEqualByComparingTo("115.000");
    }

    @Test
    void arrondiAuMillieme() {
        // (3 x 10 + 1 x 11) / 4 = 10.25 ; (3 x 10 + 4 x 11.111) / 7 = 10.6348...
        assertThat(PumpCalculator.calculer(bd("3"), bd("10"), bd("4"), bd("11.111"))).isEqualByComparingTo("10.635");
    }

    @Test
    void stockNulOuNegatifPrendLeCoutDEntree() {
        assertThat(PumpCalculator.calculer(BigDecimal.ZERO, bd("100"), bd("5"), bd("80"))).isEqualByComparingTo("80");
        assertThat(PumpCalculator.calculer(bd("-2"), bd("100"), bd("5"), bd("80"))).isEqualByComparingTo("80");
    }

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }
}
