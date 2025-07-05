package nl.dotjava.javafx.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.attach.storage.StorageService;
import nl.dotjava.javafx.domain.StorageProperty;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/** Support class to write to local private storage on mobile phone */
public class StorageSupport {
    private StorageSupport() {
        // default empty constructor
    }

    private static final String CURRENCY_HTML_FILE = "currencies.json";
    private static final String CURRENCY_USED_FILE = "currencies-used.ini";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Save currencies to local private storage as string content */
    public static void saveCurrencies(String htmlContent) {
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                StorageProperty storageProperty = new StorageProperty("html", htmlContent);
                File currenciesFile = new File(storageRoot.get(), CURRENCY_HTML_FILE);
                OBJECT_MAPPER.writeValue(currenciesFile, storageProperty);
                System.out.println("***** Currencies saved to " + currenciesFile.getAbsolutePath() + ", bytes: " + currenciesFile.length());
            }
        } catch (IOException e) {
            System.err.println("***** Error saving currencies: " + e.getMessage());
        }
    }

    /** Load currencies as flat string from local private storage or return null */
    public static String loadCurrencies() {
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                File currenciesFile = new File(storageRoot.get(), CURRENCY_HTML_FILE);
                if (currenciesFile.exists()) {
                    StorageProperty storageProperty = OBJECT_MAPPER.readValue(currenciesFile, StorageProperty.class);
                    System.out.println("***** Currencies loaded from " + currenciesFile.getAbsolutePath() + ", bytes: " + currenciesFile.length());
                    return storageProperty.getValue();
                }
            }
        } catch (IOException e) {
            System.err.println("***** Error loading currencies: " + e.getMessage());
        }
        return null;
    }

    /** Save used currencies to local private storage as string content */
    public static void saveUsedCurrencies(String from, String to) {
        StorageProperty[] properties = new StorageProperty[] {
                new StorageProperty("from", from),
                new StorageProperty("to", to)
        };
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                File usedCurrenciesFile = new File(storageRoot.get(), CURRENCY_USED_FILE);
                OBJECT_MAPPER.writeValue(usedCurrenciesFile, properties);
            }
        } catch (IOException e) {
            System.err.println("***** Error saving used currencies: " + e.getMessage());
        }
    }

    /** Load used currencies back from local private storage */
    public static String[] loadUsedCurrencies() {
        String from = "ISK";
        String to = "EUR";
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                File usedCurrenciesFile = new File(storageRoot.get(), CURRENCY_USED_FILE);
                if (usedCurrenciesFile.exists()) {
                    StorageProperty[] properties = OBJECT_MAPPER.readValue(usedCurrenciesFile, StorageProperty[].class);
                    for (StorageProperty property : properties) {
                        if ("from".equals(property.getKey())) {
                            from = property.getValue();
                        } else if ("to".equals(property.getKey())) {
                            to = property.getValue();
                        }
                    }
                } else {
                    System.out.println("***** Warning: " + CURRENCY_USED_FILE + " not found, using default ISK and EUR");
                }
            }
        } catch (IOException e) {
            System.err.println("***** Error loading currencies: " + e.getMessage());
        }
        return new String[]{from, to};
    }
}
