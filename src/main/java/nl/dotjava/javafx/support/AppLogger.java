package nl.dotjava.javafx.support;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.util.logging.Level.ALL;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;

public class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("nl.dotjava.javafx");
    private static boolean initialized = false;

    private AppLogger() {
        // utility class
    }

    public static void init(boolean verbose) {
        if (initialized) return;
        initialized = true;
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] *** %5$s%n");

        LOGGER.setLevel(ALL);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(verbose ? FINE : WARNING);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void debug(String message) {
        LOGGER.fine(message);
    }

    public static void warn(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message) {
        LOGGER.severe(message);
    }

    public static void error(String message, Throwable t) {
        LOGGER.log(Level.SEVERE, message, t);
    }
}
