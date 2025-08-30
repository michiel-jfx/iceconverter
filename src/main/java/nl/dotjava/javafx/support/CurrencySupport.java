package nl.dotjava.javafx.support;

import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyUpdateStrategy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static nl.dotjava.javafx.domain.CurrencyUpdateStrategy.FETCH_FROM_CACHE;
import static nl.dotjava.javafx.domain.CurrencyUpdateStrategy.FETCH_FROM_INTERNET;
import static nl.dotjava.javafx.domain.CurrencyUpdateStrategy.USE_DEFAULTS;
import static nl.dotjava.javafx.support.ConnectivitySupport.internetAvailable;
import static nl.dotjava.javafx.support.StorageSupport.loadIcelandicRate;
import static nl.dotjava.javafx.support.StorageSupport.saveIcelandicRate;

/**
 * Static utility class for handling currency-related operations, such as fetching web page content, extracting currency
 * rates, and converting rates to numerical values.
 */
public class CurrencySupport {
    private CurrencySupport() {
        // default empty constructor
    }

    // conversion value constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String CURRENCY_LINK = "https://www.dotjava.nl/currency_data/króna.html";
    private static final String CURRENCY_NAME = "ISK";
    private static final String CURRENCY_CODE = "kr ";
    private static final String CURRENCY_TO = "142.20";

    public static Currency getIcalandicCurrency() {
        String html = loadIcelandicRate();
        boolean hasInternet = internetAvailable();
        boolean needsFetch = shouldFetchCurrencies(html);
        Currency iceland = new Currency(CURRENCY_NAME, CURRENCY_CODE);

        return switch (getFetchStrategy(html, hasInternet, needsFetch)) {
            case FETCH_FROM_INTERNET -> {
                System.out.println("***** Refresh conversion rate from internet (outdated cache)");
                String content = downloadWebPageContentSynchronously();
                saveIcelandicRate(content);
                String valueTo = content == null
                        ? CURRENCY_TO
                        : content.replaceAll("(?s).*<body>\\s*(.*?)\\s*</body>.*", "$1").trim();
                iceland.setValueTo(new BigDecimal(valueTo));
                yield iceland;
            }
            case FETCH_FROM_CACHE -> {
                System.out.println("***** Using cached conversion rate");
                iceland.setValueTo(new BigDecimal(html.replaceAll("(?s).*<body>\\s*(.*?)\\s*</body>.*", "$1").trim()));
                yield iceland;
            }
            case USE_DEFAULTS -> {
                System.out.println("***** Using default conversion rate");
                iceland.setValueTo(new BigDecimal(CURRENCY_TO));
                yield iceland;
            }
        };
    }

    /** Determine strategy to use for fetching the conversion rate (internet, cache or defaults). */
    private static CurrencyUpdateStrategy getFetchStrategy(String html, boolean hasInternet, boolean needsFetch) {
        if (needsFetch && hasInternet) {
            return FETCH_FROM_INTERNET;
        }
        if (html != null) {
            return FETCH_FROM_CACHE;
        }
        return USE_DEFAULTS;
    }

    /**
     * Simple method to fetch a webpage synchronously and return the content as a string.
     * @return content as a string, or null if the webpage could not be fetched
     */
    protected static String downloadWebPageContentSynchronously() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CURRENCY_LINK))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                byte[] responseBody = response.body();
                return new String(responseBody, StandardCharsets.UTF_8);
            }
        } catch (IOException | UncheckedIOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Based on the cached icelandic rate file, check if it's necessary to fetch fresh data. */
    private static boolean shouldFetchCurrencies(String html) {
        if (html == null) {
            System.out.println("***** Probably first time use, no local html present");
            return true;
        }
        LocalDateTime nextMoment = LocalDateTime.parse(requireNonNull(extractTitleFromHtml(html)), DATE_FORMATTER).plusHours(6);
        // if nextMoment is in the past, the rate should be fetched again
        // if nextMoment is in the future, a refresh is not needed
        return nextMoment.isBefore(LocalDateTime.now());
    }

    /** Extract title from html currency file. */
    private static String extractTitleFromHtml(String html) {
        Pattern pattern = Pattern.compile("<title[^>]*>\\s*(.*?)\\s*</title>", java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
