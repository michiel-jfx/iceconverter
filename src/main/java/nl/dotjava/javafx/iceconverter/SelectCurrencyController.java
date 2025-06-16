package nl.dotjava.javafx.iceconverter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ResourceBundle;

import static nl.dotjava.javafx.support.CurrencySupport.getCurrencyImageFromResources;

/**
 * Controller for selecting the from and to currencies, manages communication using a listener.
 */
public class SelectCurrencyController implements Initializable {
    public SelectCurrencyController() {
        // default empty constructor
    }

    @FXML private FlowPane flagLayout;
    @FXML private ImageView fromFlag;
    @FXML private ImageView toFlag;

    private SelectCurrencyListener selectCurrencyListener;

    private int selectionCounter = 0;
    private String[] selectedCurrencies = new String[2];

    // interface for communication back to another controller
    public interface SelectCurrencyListener {
        void onCurrencyPairSelected(String fromCurrency, String toCurrency);
        void onBackToMain();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add click handlers for ImageViews in the FlowPane
        setupSelectFlagHandlers();
        System.out.println("***** SelectCurrencyController initialized");
    }

    private void setupSelectFlagHandlers() {
        // Get all ImageViews from the FlowPane and add click handlers
        flagLayout.getChildren().forEach(node -> {
            if (node instanceof ImageView imageView) {
                imageView.setOnMouseClicked(this::handleFlagSelected);
            }
        });
    }

    @FXML
    private void handleFlagSelected(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView clickedImage = (ImageView) event.getSource();
            String imageUrl = clickedImage.getImage().getUrl();
            String currencyCode = extractCurrencyCodeFromUrl(imageUrl);

            if (currencyCode != null && this.selectCurrencyListener != null) {
                selectedCurrencies[selectionCounter] = currencyCode;
                selectionCounter++;

                System.out.println("***** Currency " + selectionCounter + " selected: " + currencyCode);

                if (selectionCounter == 1) {
                    // first selection done, update the "from" flag and continue
                    fromFlag.setImage(getCurrencyImageFromResources("flag/small", currencyCode));
                    System.out.println("***** Now select target currency");
                } else if (selectionCounter == 2) {
                    // second selection done, update the "to" flag and finish
                    toFlag.setImage(getCurrencyImageFromResources("flag/small", currencyCode));
                    // add a small delay to allow the UI to update before switching scenes
                    Platform.runLater(() -> {
                        try {
                            // Small delay to show the second flag update
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("***** Both currencies selected: " + selectedCurrencies[0] + " and " + selectedCurrencies[1]);
                        // notify listener with both currencies
                        this.selectCurrencyListener.onCurrencyPairSelected(selectedCurrencies[0], selectedCurrencies[1]);
                    });
                }
            }
        }
    }

    /** extract currency code from URL like "file:/.../flag/small/EUR.png" */
    private String extractCurrencyCodeFromUrl(String url) {
        try {
            int lastSlash = url.lastIndexOf('/');
            int lastDot = url.lastIndexOf('.');
            if (lastSlash != -1 && lastDot != -1 && lastDot > lastSlash) { return url.substring(lastSlash + 1, lastDot); }
        } catch (Exception e) {
            System.err.println("Error extracting currency code from URL: " + url);
        }
        return null;
    }

    public void setSelectCurrencyListener(SelectCurrencyListener listener) {
        this.selectCurrencyListener = listener;
    }

    /**
     * Sets the current currencies by configuring their respective flag images. Also resets the selection counter.
     * @param from the currency code representing the "from" currency
     * @param to the currency code representing the "to" currency
     */
    public void setCurrentCurrencies(String from, String to) {
        selectionCounter = 0;
        selectedCurrencies[0] = null;
        selectedCurrencies[1] = null;
        fromFlag.setImage(getCurrencyImageFromResources("flag/small", from));
        toFlag.setImage(getCurrencyImageFromResources("flag/small", to));
        System.out.println("***** SelectCurrencyController: Selection reset. Flags set to " + from + " -> " + to);
    }
}
