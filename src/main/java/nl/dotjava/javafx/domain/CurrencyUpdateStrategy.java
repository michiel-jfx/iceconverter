package nl.dotjava.javafx.domain;

public enum CurrencyUpdateStrategy {
    /** Fetch conversion rate from internet 'cause they are outdated. */
    FETCH_FROM_INTERNET,
    /** Use conversion rate cache (rate is fresh or no internet). */
    FETCH_FROM_CACHE,
    /** Use default conversion rate (defaulted to 142.20), there is a problem. */
    USE_DEFAULTS
}
