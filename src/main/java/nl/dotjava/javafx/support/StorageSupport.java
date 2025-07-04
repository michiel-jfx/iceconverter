package nl.dotjava.javafx.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.attach.storage.StorageService;
import nl.dotjava.javafx.domain.StorageProperty;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class StorageSupport {
    private StorageSupport() {
        // default empty constructor
    }

    private static final String CURRENCY_HTML_FILE = "currencies.json";
    private static final String CURRENCY_FROM_FILE = "used-from.ini";
    private static final String CURRENCY_TO_FILE = "used-to.ini";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** Save currencies to local (private) storage as string content */
    public static void saveCurrencies(String htmlContent) {
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                StorageProperty storageProperty = new StorageProperty("html", htmlContent);
                File currenciesFile = new File(storageRoot.get(), CURRENCY_HTML_FILE);
                OBJECT_MAPPER.writeValue(currenciesFile, storageProperty);
                System.out.println("***** Currencies saved to " + currenciesFile.getAbsolutePath() + ", bytes: " + currenciesFile.length());
            } else {
                System.out.println("***** Error saving currencies: Storage root not found");
            }
        } catch (IOException e) {
            System.err.println("***** Error saving currencies: " + e.getMessage());
        }
    }

    /** Load currencies as flat string from local storage or return null */
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
            } else {
                System.out.println("***** Error loading currencies: Storage root not found");
                return null;
            }
        } catch (IOException e) {
            System.err.println("***** Error loading currencies: " + e.getMessage());
        }
        return null;
    }

    /** Save used currencies to local (private) storage as string content */
    public static void saveUsedCurrencies(String from, String to) {
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                // store 'from' setting
                StorageProperty fromProperty = new StorageProperty("from", from);
                File fromFile = new File(storageRoot.get(), CURRENCY_FROM_FILE);
                OBJECT_MAPPER.writeValue(fromFile, fromProperty);
                // store 'to' setting
                StorageProperty toProperty = new StorageProperty("to", to);
                File toFile = new File(storageRoot.get(), CURRENCY_TO_FILE);
                OBJECT_MAPPER.writeValue(toFile, toProperty);
            }
        } catch (IOException e) {
            System.err.println("***** Error saving used currencies: " + e.getMessage());
        }
    }

    /** Load used currencies as flat string from local storage, return both concatenated */
    public static String[] loadUsedCurrencies() {
        String from = "ISK";
        String to = "EUR";
        try {
            Optional<File> storageRoot = StorageService.create().flatMap(StorageService::getPrivateStorage);
            if (storageRoot.isPresent()) {
                // load 'from' setting
                File fromFile = new File(storageRoot.get(), CURRENCY_FROM_FILE);
                if (fromFile.exists()) {
                    StorageProperty storageProperty = OBJECT_MAPPER.readValue(fromFile, StorageProperty.class);
                    from = storageProperty.getValue();
                } else {
                    System.out.println("***** Warning: used-from.ini not found, using default ISK");
                }
                // load 'to' setting
                File toFile = new File(storageRoot.get(), CURRENCY_TO_FILE);
                if (toFile.exists()) {
                    StorageProperty storageProperty = OBJECT_MAPPER.readValue(toFile, StorageProperty.class);
                    to = storageProperty.getValue();
                } else {
                    System.out.println("***** Warning: used-to.ini not found, using default EUR");
                }
            }
        } catch (IOException e) {
            System.err.println("***** Error loading currencies: " + e.getMessage());
        }
        return new String[]{from, to};
    }
}
