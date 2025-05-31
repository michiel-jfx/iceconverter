package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.CurrencyRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            BigDecimal value = new BigDecimal(currencyValue.replace(",", ".")).setScale(3, RoundingMode.HALF_UP);
            return currencyRate.getCurrencySymbol() + value.multiply(currencyRate.getValueTo()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return OOPS;
        }
    }

    public String convertToEuroCurrency(String euroValue) {
        try {
            BigDecimal value = new BigDecimal(euroValue.replace(",", ".")).setScale(3, RoundingMode.HALF_UP);
            return currencyRate.getTargetSymbol() + value.multiply(currencyRate.getValueFrom()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return OOPS;
        }
    }
}
