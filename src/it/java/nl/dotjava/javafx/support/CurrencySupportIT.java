package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.dotjava.javafx.support.CurrencySupport.extractAllCurrenciesFromSite;
import static org.assertj.core.api.Assertions.assertThat;

class CurrencySupportIT {

    @Test
    void testExtractAllCurrenciesFromSite() {
        List<Currency> currencies = extractAllCurrenciesFromSite();
        assertThat(currencies)
                .isNotNull()
                .isNotEmpty();

        assertThat(currencies)
                .extracting(Currency::getName)
                .contains("ISK");

        // Verify that each currency has the expected properties
        currencies.forEach(currency -> {
            assertThat(currency.getName()).isNotNull().isNotEmpty();
            assertThat(currency.getValueFrom() != null || currency.getValueTo() != null)
                    .as("Either valueFrom or valueTo should be set for currency " + currency.getName())
                    .isTrue();
        });

        // list of currencies should at least contain the Icelandic currency
        Currency isk = currencies.stream()
                .filter(c -> "ISK".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(isk).isNotNull();
        assertThat(isk.getValueFrom().doubleValue()).isBetween(0.005, 0.01);
        assertThat(isk.getValueTo().doubleValue()).isBetween(100.0, 200.0);
    }
}
