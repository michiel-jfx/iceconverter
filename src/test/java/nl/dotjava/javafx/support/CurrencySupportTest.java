package nl.dotjava.javafx.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CurrencySupportTest {

    @Test
    void testConvertRateToBigDecimal() {
        // normal case
        BigDecimal result = CurrencySupport.convertRateToBigDecimal("146,72");
        assertThat(result).isEqualTo(new BigDecimal("146.72"));
        // edge cases
        assertThat(CurrencySupport.convertRateToBigDecimal(null)).isNull();
        assertThat(CurrencySupport.convertRateToBigDecimal("")).isNull();
        assertThat(CurrencySupport.convertRateToBigDecimal("invalid")).isNull();
    }
}
