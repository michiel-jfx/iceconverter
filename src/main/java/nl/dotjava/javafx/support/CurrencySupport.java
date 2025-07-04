package nl.dotjava.javafx.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import nl.dotjava.javafx.iceconverter.IceController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.net.http.HttpClient.newHttpClient;
import static nl.dotjava.javafx.support.StorageSupport.loadCurrencies;
import static nl.dotjava.javafx.support.StorageSupport.saveCurrencies;

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
    private static final BigDecimal ICELAND_FROM = new BigDecimal("0.0068");
    private static final String VALUE_FROM = "valueFrom";
    private static final String VALUE_TO = "valueTo";

    /**
     * Fetch currency data from <a href="https://www.dotJava.nl/currency_data/currencies.html">www.dotJava.nl</a>. When
     * this fails, try to load from local storage. When everything fails, return a default.
     * @return list of currencies found on website or defaulted to ISK
     */
    public static List<CurrencyRate> extractAllCurrenciesFromSite() {
        String html = downloadWebPageContentSynchronously();
        if (html == null || html.isEmpty()) {
            html = loadCurrencies();
        } else {
            saveCurrencies(html);
        }
        List<CurrencyRate> currencies = extractFromHtml(html);
        if (currencies.isEmpty()) {
            CurrencyRate iceland = new CurrencyRate(Currency.ISK);
            iceland.setValueFrom(ICELAND_FROM);
            currencies.add(iceland);
        }
        return currencies;
    }

    /** Extract HTML data found to list of currencies */
    private static List<CurrencyRate> extractFromHtml(String html) {
        List<CurrencyRate> currencies = new ArrayList<>();
        try {
            // extract content between <body> and </body>
            String data = html.replaceAll("(?s).*<body>\\s*(.*?)\\s*</body>.*", "$1").trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonArray = mapper.readTree(data);
            for (JsonNode node : jsonArray) {
                String cur = node.get("currency").asText();
                CurrencyRate currencyRate = new CurrencyRate(Currency.valueOf(cur));

                if (node.has(VALUE_TO) && !node.get(VALUE_TO).isNull()) {
                    BigDecimal valueTo = new BigDecimal(node.get(VALUE_TO).asText());
                    currencyRate.setValueTo(valueTo);
                } else {
                    if (node.has(VALUE_FROM) && !node.get(VALUE_FROM).isNull()) {
                        BigDecimal valueFrom = new BigDecimal(node.get(VALUE_FROM).asText());
                        currencyRate.setValueFrom(valueFrom);
                    }
                }
                currencies.add(currencyRate);
            }
        } catch (Exception e) {
            System.err.println("Error parsing currency data: " + e.getMessage());
            e.printStackTrace();
        }
        return currencies;
    }

    /**
     * Simple method to fetch a webpage synchronously and return the content as a string.
     * @return content as a string, or null if the webpage could not be fetched
     */
    protected static String downloadWebPageContentSynchronously() {
        try (HttpClient httpClient = newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ALL_CURRENCIES))
                    .build();

            try {
                HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() == 200) {
                    byte[] responseBody = response.body();
                    System.out.println("***** Fetched webpage (" + ALL_CURRENCIES + ") successfully");
                    return new String(responseBody, StandardCharsets.UTF_8);
                }
            } catch (IOException | InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Get flag or symbol images from resources (based on iceconverter package location).
     */
    public static Image getCurrencyImageFromResources(String folder, String resource) {
        try {
            return new Image(IceController.class.getResourceAsStream(folder + "/" + resource + ".png"));
        } catch (Exception e) {
            System.err.println("Error loading image (" + resource + "): " + e.getMessage());
        }
        return null;
    }
}
