package nl.dotjava.javafx.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dotjava.javafx.domain.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static utility class for handling currency-related operations, such as fetching web page content, extracting currency
 * rates, and converting rates to numerical values.
 */
public class CurrencySupport {
    private CurrencySupport() {
        // default empty constructor
    }

    // conversion value constants
    private static final String ALL_CURRENCIES = "https://www.dotjava.nl/currency_data/currencies.html";
    private static final String ICELAND_NAME = "ISK";
    private static final String ICELAND_CUR = "kr ";
    private static final String ICELAND_URL = "https://sedlabanki.is/";
    private static final BigDecimal ICELAND_FROM = new BigDecimal("0.0068");

    private static final String VALUE_FROM = "valueFrom";
    private static final String VALUE_TO = "valueTo";

    /**
     * Simple method to fetch a webpage synchronously and return the content as a string.
     * @param url page to fetch
     * @return content as a string, or null if the webpage could not be fetched
     */
    protected static String downloadWebPageContentSynchronously(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                byte[] responseBody = response.body();
                System.out.println("***** Fetched webpage content (" + url + ") successfully");
                return new String(responseBody, StandardCharsets.UTF_8);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts the EUR currency rate from the webpage content. Searches for a pattern where EUR currency is defined and
     * extracts the rate value.
     * @param content The (HTML) content of the webpage
     * @return the EUR currency rate as a string, or null if not found
     */
    protected static String extractEurRateFromSite(String content) {
        if (content == null || content.isEmpty()) { return null; }
        // look for "currency-flags/eur.png" followed by ">EUR</span>" and then the rate in the upcoming span
        String pattern = "currency-flags/eur\\.png.*?>EUR</span>\\s*<span class=\"currency-rate\">(.*?)</span>";
        Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = r.matcher(content);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    /**
     * Converts a currency rate string to BigDecimal. For example, converts "146,70" to a BigDecimal representation of 146.70
     * @param rate The currency rate as a string (e.g., "146,70")
     * @return value as a BigDecimal, or null if the input is invalid
     */
    protected static BigDecimal convertRateToBigDecimal(String rate) {
        if (rate == null || rate.isEmpty()) { return null; }
        try {
            String normalizedRate = rate.replace(',', '.');
            return new BigDecimal(normalizedRate);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * In the first version, only a fetch from Seðlabanki Íslands was implemented by searching the website for a pattern.
     * When it fails, initialize a default conversion rate for the ISK currency.
     * @return list of currencies with ISK entry
     */
    private static List<Currency> initializeIcelandicCurrency() {
        Currency iceland = new Currency(ICELAND_NAME, ICELAND_CUR);
        String rate = extractEurRateFromSite(downloadWebPageContentSynchronously(ICELAND_URL));
        if (rate != null) {
            System.out.println("***** Icelandic currency rate found: " + rate);
            iceland.setValueTo(convertRateToBigDecimal(rate));
        } else {
            // if not found (or no network connection)
            iceland.setValueFrom(ICELAND_FROM);
        }
        return Collections.singletonList(iceland);
    }

    /**
     * Fetch currency data from <a href="https://www.dotJava.nl/currency_data/currencies.html">www.dotJava.nl</a>. When
     * this fails, try to load it from Seðlabanki Íslands.
     * @return list of currencies
     */
    public static List<Currency> extractAllCurrenciesFromSite() {
        List<Currency> currencies = new ArrayList<>();
        String html = downloadWebPageContentSynchronously(ALL_CURRENCIES);
        if (html == null || html.isEmpty()) {
            System.out.println("***** Currency data not found");
            return initializeIcelandicCurrency();
        }
        try {
            // extract content between <body> and </body>
            String data = html.replaceAll("(?s).*<body>\\s*(.*?)\\s*</body>.*", "$1").trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonArray = mapper.readTree(data);
            for (JsonNode node : jsonArray) {
                String name = node.get("name").asText();
                String currencyCode = node.get("currencyCode").asText();
                Currency currency = new Currency(name, currencyCode);

                if (node.has(VALUE_TO) && !node.get(VALUE_TO).isNull()) {
                    BigDecimal valueTo = new BigDecimal(node.get(VALUE_TO).asText());
                    currency.setValueTo(valueTo);
                } else {
                    if (node.has(VALUE_FROM) && !node.get(VALUE_FROM).isNull()) {
                        BigDecimal valueFrom = new BigDecimal(node.get(VALUE_FROM).asText());
                        currency.setValueFrom(valueFrom);
                    }
                }
                currencies.add(currency);
                System.out.println("***** Added currency: " + currency);
            }
        } catch (Exception e) {
            System.err.println("Error parsing currency data: " + e.getMessage());
            e.printStackTrace();
        }
        return currencies;
    }
}
