package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import org.junit.jupiter.api.Test;

import static nl.dotjava.javafx.support.CurrencySupport.getIcalandicCurrency;
import static org.assertj.core.api.Assertions.assertThat;

class CurrencySupportIT {

    @Test
    void testExtractIcelandicCurrencyFromSite() {
        Currency currency = getIcalandicCurrency();
        assertThat(currency).isNotNull();
        assertThat(currency.getValueFrom().doubleValue()).isBetween(0.005, 0.01);
        assertThat(currency.getValueTo().doubleValue()).isBetween(100.0, 200.0);
    }
}
