package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConvertSupportTest {

    private static CurrencyRate currencyRate;
    private ConvertSupport convertSupport;

    @BeforeEach
    void setUpEach() {
        currencyRate = new CurrencyRate(Currency.ANG);
        currencyRate.setValueFrom(new BigDecimal("0.5182"));
        convertSupport = new ConvertSupport();
        convertSupport.setCurrency(currencyRate);
    }

    @Test
    void testConvertToOtherCurrency() {
        String result = convertSupport.convertToOtherCurrency("10");
        assertThat(result).isEqualTo("ƒ 19.30");

        String resultWithComma = convertSupport.convertToOtherCurrency("10,5");
        assertThat(resultWithComma).isEqualTo("ƒ 20.26");

        String resultInvalid = convertSupport.convertToOtherCurrency("not-a-number");
        assertThat(resultInvalid).isEmpty();
    }

    @Test
    void testConvertToEuroCurrency() {
        String result = convertSupport.convertToEuroCurrency("1000");
        assertThat(result).isEqualTo("€ 518.20");

        String resultWithComma = convertSupport.convertToEuroCurrency("1000,5");
        assertThat(resultWithComma).isEqualTo("€ 518.46");

        String resultInvalid = convertSupport.convertToEuroCurrency("invalid");
        assertThat(resultInvalid).isEmpty();
    }

    @Test
    void testCurrencyOutput() {
        assertThat(currencyRate.toString()).hasToString("[ANG: from ƒ 0.5182, to € 1.929756851]");
        currencyRate.setTargetSymbol("x ");
        assertThat(currencyRate.toString()).hasToString("[ANG: from ƒ 0.5182, to x 1.929756851]");
    }
}
