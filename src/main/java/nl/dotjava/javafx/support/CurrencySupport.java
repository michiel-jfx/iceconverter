package nl.dotjava.javafx.support;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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

    /**
     * Simple method to fetch a webpage synchronously and return the content as a string.
     * @param url page to fetch
     * @return content as a string, or null if the webpage could not be fetched
     */
    public static String downloadWebPageContentSynchronously(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                byte[] responseBody = response.body();
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
    public static String extractEurRate(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
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
     * Converts a currency rate string to BigDecimal.
     * For example, converts "146,70" to a BigDecimal representation of 146.70
     * @param rate The currency rate as a string (e.g., "146,70")
     * @return value as a BigDecimal, or null if the input is invalid
     */
    public static BigDecimal convertRateToBigDecimal(String rate) {
        if (rate == null || rate.isEmpty()) {
            return null;
        }
        try {
            String normalizedRate = rate.replace(',', '.');
            return new BigDecimal(normalizedRate);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
