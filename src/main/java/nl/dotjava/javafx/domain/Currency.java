package nl.dotjava.javafx.domain;

public enum Currency {
    EUR("Europese euro", "€ "),
    USD("Amerikaanse dollar", "$ "),
    ANG("Antillaanse gulden", "ƒ "),
    AUD("Australische dollar", "A$ "),
    CAD("Canadese dollar", "C$ "),
    CNY("Chinese yuan", "CN¥ "),
    DKK("Deense kroon", "kr "),
    GBP("Engelse pond", "£ "),
    HKD("Kong dollar", "HK$ "),
    HUF("Hongaarse forint", "Ft "),
    ILS("Israëlische shekel", "₪ "),
    JPY("Japanse yen", "¥ "),
    MAD("Marokkaanse dirham", " د.م."),
    NOK("Noorse kroon", "kr "),
    NZD("N-Zeelandse dollar", "NZ$ "),
    PLN("Poolse zloty", "zł "),
    CZK("Tsjechische kroon", "Kč "),
    TRY("Turkse lira", "₺ "),
    ZAR("Z-Afrikaanse rand", "R "),
    SEK("Zweedse kroon", "kr "),
    CHF("Zwitserse frank", "Fr "),
    ISK("IJslandse kroon", "kr ");

    private final String description;
    private final String symbol;

    Currency(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
