package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.CurrencyRate;

import java.math.BigDecimal;

public class ConvertSupport {

    private static final String OOPS = "úps";
    private CurrencyRate currencyRate;

    public ConvertSupport() {
        // instantiated during initializing, later the currencyRate is set
    }

    public void setCurrency(CurrencyRate currencyRate) {
        this.currencyRate = currencyRate;
    }

    public String convertToOtherCurrency(String currencyValue) {
        try {
            BigDecimal value = new BigDecimal(currencyValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return currencyRate.getCurrencySymbol() + value.multiply(currencyRate.getValueTo()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }

    public String convertToEuroCurrency(String euroValue) {
        try {
            BigDecimal value = new BigDecimal(euroValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return "€ " + value.multiply(currencyRate.getValueFrom()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }
}
