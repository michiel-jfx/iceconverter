package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConvertSupportTest {

    private static CurrencyRate currencyRate;
    private ConvertSupport convertSupport;

    @BeforeAll
    static void setUp() {
        currencyRate = new CurrencyRate(Currency.ANG);
        currencyRate.setValueFrom(new BigDecimal("0.1234"));
    }

    @BeforeEach
    void setUpEach() {
        convertSupport = new ConvertSupport();
        convertSupport.setCurrency(currencyRate);
    }

    @Test
    void testConvertToOtherCurrency() {
        String result = convertSupport.convertToOtherCurrency("10");
        assertThat(result).isEqualTo("ƒ 81.04");

        String resultWithComma = convertSupport.convertToOtherCurrency("10,5");
        assertThat(resultWithComma).isEqualTo("ƒ 85.09");

        String resultInvalid = convertSupport.convertToOtherCurrency("not-a-number");
        assertThat(resultInvalid).isEqualTo("úps");
    }

    @Test
    void testConvertToEuroCurrency() {
        String result = convertSupport.convertToEuroCurrency("1000");
        assertThat(result).isEqualTo("€ 123.40");

        String resultWithComma = convertSupport.convertToEuroCurrency("1000,5");
        assertThat(resultWithComma).isEqualTo("€ 123.46");

        String resultInvalid = convertSupport.convertToEuroCurrency("invalid");
        assertThat(resultInvalid).isEqualTo("úps");
    }

    @Test
    void testCurrencyOutput() {
        assertThat(currencyRate.toString()).hasToString("[ANG (ƒ ), from: 0.1234, to: 8.103727715]");
    }
}
