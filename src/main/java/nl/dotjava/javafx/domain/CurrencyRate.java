package nl.dotjava.javafx.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CurrencyRate {

    private final Currency currency;
    private BigDecimal valueFrom;
    private BigDecimal valueTo;
    private String targetSymbol;

    public CurrencyRate(Currency currency) {
        this.currency = currency;
        this.targetSymbol = null;
    }

    public String getName() {
        return this.currency.name();
    }

    public String getCurrencySymbol() {
        return this.currency.getSymbol();
    }

    public BigDecimal getValueFrom() {
        return this.valueFrom;
    }

    public BigDecimal getValueTo() {
        return this.valueTo;
    }

    public void setTargetSymbol(String targetSymbol) {
        this.targetSymbol = targetSymbol;
    }

    public String getTargetSymbol() {
        return this.targetSymbol == null ? "€ " : this.targetSymbol;
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
            return BigDecimal.ONE.divide(value, new MathContext(10, RoundingMode.HALF_UP));
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + this.currency.name() + " (" + this.currency.getSymbol() + "), from: " + this.valueFrom + ", to: " + this.valueTo + "]";
    }
}
