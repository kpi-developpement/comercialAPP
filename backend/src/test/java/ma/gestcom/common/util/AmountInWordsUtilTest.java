package ma.gestcom.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AmountInWordsUtilTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "0|zéro",
        "21|vingt et un",
        "71|soixante et onze",
        "80|quatre-vingts",
        "81|quatre-vingt-un",
        "91|quatre-vingt-onze",
        "200|deux cents",
        "201|deux cent un",
        "1000|mille",
        "1980|mille neuf cent quatre-vingts",
        "80000|quatre-vingt mille",
        "200000|deux cent mille",
        "2000000|deux millions",
        "1234567|un million deux cent trente-quatre mille cinq cent soixante-sept"
    })
    void nombres(long valeur, String attendu) {
        assertThat(AmountInWordsUtil.numberToWords(valeur)).isEqualTo(attendu);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "1|Un dirham",
        "1280.50|Mille deux cent quatre-vingts dirhams et cinquante centimes",
        "12000.01|Douze mille dirhams et un centime"
    })
    void montants(String montant, String attendu) {
        assertThat(AmountInWordsUtil.toWords(new BigDecimal(montant))).isEqualTo(attendu);
    }
}
