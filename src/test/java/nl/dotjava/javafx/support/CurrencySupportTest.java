package nl.dotjava.javafx.support;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CurrencySupportTest {

    @Test
    void testExtractEurRateFromSiteFromSampleHtml() {
        String sampleHtml = "<!DOCTYPE html><html><body><div class=\"popular-currencies-content\">" +
                "<ul class=\"popular-currencies-list\"><li><div class=\"currency-card currency-card--show-flag\">" +
                "<span class=\"currency-name\" style=\"--flag: url('/library/template/currency-flags/eur.png')\">EUR</span>" +
                "<span class=\"currency-rate\">146,71</span></div></li></ul></body></html>";

        String eurRate = CurrencySupport.extractEurRateFromSite(sampleHtml);
        assertThat(eurRate).isEqualTo("146,71");
    }

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
