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
        currency.setValueFrom(new BigDecimal("0.0067"));
    }

    @BeforeEach
    void setUpEach() {
        convertSupport = new ConvertSupport();
        convertSupport.setCurrency(currency);
    }

    @Test
    void testConvertToOtherCurrency() {
        String result = convertSupport.convertToOtherCurrency("10");
        assertThat(result).isEqualTo("kr 1492.54");

        String resultWithComma = convertSupport.convertToOtherCurrency("10,5");
        assertThat(resultWithComma).isEqualTo("kr 1567.16");

        String resultInvalid = convertSupport.convertToOtherCurrency("not-a-number");
        assertThat(resultInvalid).isEqualTo("kr 0");
    }

    @Test
    void testConvertToEuroCurrency() {
        String result = convertSupport.convertToEuroCurrency("1000");
        assertThat(result).isEqualTo("€ 6.70");

        String resultWithComma = convertSupport.convertToEuroCurrency("1000,5");
        assertThat(resultWithComma).isEqualTo("€ 6.70");

        String resultInvalid = convertSupport.convertToEuroCurrency("invalid");
        assertThat(resultInvalid).isEqualTo("€ 0");
    }

    @Test
    void testCurrencyOutput() {
        assertThat(currency.toString()).hasToString("[TST (kr ), from: 0.0067, to: 149.2537313]");
    }
}
