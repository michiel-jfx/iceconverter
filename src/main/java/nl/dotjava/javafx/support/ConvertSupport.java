package nl.dotjava.javafx.support;

import java.math.BigDecimal;

public class ConvertSupport {
    private ConvertSupport() {
        // default empty constructor
    }
    private static final String OOPS = "úps";
    private static final BigDecimal FACTOR_FROM = new BigDecimal("0.0068");
    private static final BigDecimal FACTOR_TO = new BigDecimal("147.06");

    public static String convertToOtherCurrency(String currencyValue) {
        try {
            BigDecimal value = new BigDecimal(currencyValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return "kr " + value.multiply(FACTOR_TO).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }
    public static String convertToEuroCurrency(String euroValue) {
        try {
            BigDecimal value = new BigDecimal(euroValue.replace(",", ".")).setScale(3, BigDecimal.ROUND_HALF_UP);
            return "€ " + value.multiply(FACTOR_FROM).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            return OOPS;
        }
    }
}
