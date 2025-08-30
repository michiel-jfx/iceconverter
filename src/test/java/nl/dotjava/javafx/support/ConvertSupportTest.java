package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConvertSupportTest {

    private static Currency currency;
    private ConvertSupport convertSupport;

    @BeforeAll
    static void setUp() {
        currency = new Currency("TST", "kr ");
        currency.setValueTo(new BigDecimal("142.20"));
    }

    @BeforeEach
    void setUpEach() {
        convertSupport = new ConvertSupport();
        convertSupport.setCurrency(currency);
    }

    @Test
    void testConvertToOtherCurrency() {
        String result = convertSupport.convertToOtherCurrency("10");
        assertThat(result).isEqualTo("kr 1422.00");

        String resultWithComma = convertSupport.convertToOtherCurrency("10,5");
        assertThat(resultWithComma).isEqualTo("kr 1493.10");

        String resultInvalid = convertSupport.convertToOtherCurrency("not-a-number");
        assertThat(resultInvalid).isEqualTo("kr 0");
    }

    @Test
    void testConvertToEuroCurrency() {
        String result = convertSupport.convertToEuroCurrency("1000");
        assertThat(result).isEqualTo("€ 7.03");

        String resultWithComma = convertSupport.convertToEuroCurrency("1000,5");
        assertThat(resultWithComma).isEqualTo("€ 7.04");

        String resultInvalid = convertSupport.convertToEuroCurrency("invalid");
        assertThat(resultInvalid).isEqualTo("€ 0");
    }

    @Test
    void testCurrencyOutput() {
        assertThat(currency.toString()).hasToString("[TST (kr ), from: 0.007032348805, to: 142.20]");
    }
}
