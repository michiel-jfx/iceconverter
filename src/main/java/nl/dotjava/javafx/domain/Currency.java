package nl.dotjava.javafx.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Currency {
    private final String name;
    private final String currencyCode;
    private BigDecimal valueFrom;
    private BigDecimal valueTo;

    public Currency(String name) {
        this.name = name;
        this.currencyCode = "€ ";
    }

    public Currency(String name, String currencyCode) {
        this.name = name;
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public BigDecimal getValueFrom() {
        return this.valueFrom;
    }

    public BigDecimal getValueTo() {
        return this.valueTo;
    }

    public void setValueTo(BigDecimal valueTo) {
        this.valueTo = valueTo;
        this.valueFrom = calculateReciprocal(this.valueTo);
    }

    private BigDecimal calculateReciprocal(BigDecimal value) {
        if (value != null && value.compareTo(BigDecimal.ZERO) > 0) {
            return BigDecimal.ONE.divide(value, new MathContext(10, RoundingMode.HALF_UP));
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + this.name + " (" + this.currencyCode + "), from: " + this.valueFrom + ", to: " + this.valueTo + "]";
    }
}
