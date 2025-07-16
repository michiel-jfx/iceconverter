package nl.dotjava.javafx.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dotjava.javafx.domain.Currency;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static final String VALUE_FROM = "valueFrom";
    private static final String VALUE_TO = "valueTo";
    private static final BigDecimal ICELAND_FROM = new BigDecimal("0.0068");

    /**
     * Fetch currency data from <a href="https://www.dotJava.nl/currency_data/currencies.html">www.dotJava.nl</a>.
     * When this fails, initialize a default conversion rate for the ISK currency.
     * @return list of currencies
     */
    public static List<Currency> extractAllCurrenciesFromSite() {
        List<Currency> currencies = new ArrayList<>();
        String html = downloadWebPageContentSynchronously();
        if (html == null || html.isEmpty()) {
            System.out.println("***** Currency data not found");
            Currency iceland = new Currency(ICELAND_NAME, ICELAND_CUR);
            iceland.setValueFrom(ICELAND_FROM);
            return Collections.singletonList(iceland);
        }
        try {
            // extract content between <body> and </body>
            String data = html.replaceAll("(?s).*<body>\\s*(.*?)\\s*</body>.*", "$1").trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonArray = mapper.readTree(data);
            for (JsonNode node : jsonArray) {
                String name = node.get("currency").asText();
                Currency currency = new Currency(name);
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
            }
        } catch (Exception e) {
            System.err.println("***** Error parsing currency data: " + e.getMessage());
            e.printStackTrace();
        }
        return currencies;
    }

    /**
     * Simple method to fetch a webpage synchronously and return the content as a string.
     * @return content as a string, or null if the webpage could not be fetched
     */
    protected static String downloadWebPageContentSynchronously() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ALL_CURRENCIES))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                byte[] responseBody = response.body();
                System.out.println("***** Fetched webpage content (" + ALL_CURRENCIES + ") successfully");
                return new String(responseBody, StandardCharsets.UTF_8);
            }
        } catch (IOException | UncheckedIOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
