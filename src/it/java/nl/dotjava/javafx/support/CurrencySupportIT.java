package nl.dotjava.javafx.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CurrencySupportIT {

    @Test
    void testFetchWebPageContentSynchronously() {
        String url = "https://sedlabanki.is/";
        String result = CurrencySupport.downloadWebPageContentSynchronously(url);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }

    @Test
    void testExtractEurRate() {
        String url = "https://sedlabanki.is/";
        String result = CurrencySupport.downloadWebPageContentSynchronously(url);
        String eurRate = CurrencySupport.extractEurRate(result);

        assertThat(eurRate).isNotNull();
        assertThat(eurRate).matches("\\d+,\\d+"); // matches format like "146,70"
    }
}
