package nl.dotjava.javafx.domain;

import java.math.BigDecimal;

public class Currency {
    private final String name;
    private final String currencyCode;
    private BigDecimal valueFrom;
    private BigDecimal valueTo;

    public Currency(String name, String currencyCode) {
        this.name = name;
        this.currencyCode = currencyCode;
    }

    public BigDecimal getValueFrom() {
        return this.valueFrom;
    }

    public BigDecimal getValueTo() {
        return this.valueTo;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setValueFrom(BigDecimal valueFrom) {
        this.valueFrom = valueFrom;
        this.valueTo = calculateReciprocal(this.valueFrom);
    }

    public void setValueTo(BigDecimal valueTo) {
        this.valueTo = valueTo;
        this.valueFrom = calculateReciprocal(this.valueTo);
    }

    private BigDecimal calculateReciprocal(BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) > 0) {
            return BigDecimal.ONE.divide(value, 10, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}
