package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;

import java.math.BigDecimal;

public class ConvertSupport {

    private static final String OOPS = "úps";
    private Currency currency;

    public ConvertSupport() {
        // instantiated during initializing, later the currency is set
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String convertToOtherCurrency(String currencyValue) {
        try {
            BigDecimal value = new BigDecimal(currencyValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return "kr " + value.multiply(currency.getValueTo()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }

    public String convertToEuroCurrency(String euroValue) {
        try {
            BigDecimal value = new BigDecimal(euroValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return "€ " + value.multiply(currency.getValueFrom()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }
}
