package nl.dotjava.javafx.iceconverter;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.support.ConvertSupport;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ESCAPE;

public class IceController implements Initializable {

    public IceController() {
        System.out.println("***** IceController instantiated");
    }

    // portrait
    @FXML private TextField textfieldInput;
    @FXML private Label labelUpperRight;
    @FXML private Label labelBelowLeft;
    // landscape
    @FXML private TextField textfieldInputLandscape;
    @FXML private Label labelUpperRightLandscape;
    @FXML private Label labelBelowLeftLandscape;
    // layout containers
    @FXML private VBox portraitLayout;
    @FXML private VBox landscapeLayout;

    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);
    private final ConvertSupport convertSupport = new ConvertSupport();
    private final HashMap<String,Currency> currencyMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // bind visibility of layouts to the orientation property
        portraitLayout.visibleProperty().bind(isPortrait);
        portraitLayout.managedProperty().bind(isPortrait);
        landscapeLayout.visibleProperty().bind(isPortrait.not());
        landscapeLayout.managedProperty().bind(isPortrait.not());
        // bidirectional binding
        textfieldInput.textProperty().bindBidirectional(textfieldInputLandscape.textProperty());
        // unidirectional binding
        labelUpperRightLandscape.textProperty().bind(labelUpperRight.textProperty());
        labelBelowLeftLandscape.textProperty().bind(labelBelowLeft.textProperty());
        setupBackButtonHandler();
        System.out.println("***** IceController initialized");
    }

    @FXML
    protected void onCurrencyClick() {
        if (textfieldInput != null) {
            String inputText = textfieldInput.getText();
            try {
                labelUpperRight.setText(convertSupport.convertToOtherCurrency(inputText));
                labelBelowLeft.setText(convertSupport.convertToEuroCurrency(inputText));
            } catch (Exception e) {
                System.err.println("Error during conversion: " + e.getMessage());
            }
        }
    }

    /** Add handler to scene to catch the Android previous or back tapping event. */
    private void setupBackButtonHandler() {
        // ensure the scene is fully loaded before adding the key-handler
        Platform.runLater(() -> {
            if (portraitLayout.getScene() != null) {
                portraitLayout.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == ESCAPE || event.getCode() == BACK_SPACE) {
                        Platform.exit();
                        System.exit(0);
                        event.consume();
                    }
                });
            }
        });
    }

    protected void setPortraitModus(boolean portrait) {
        isPortrait.set(portrait);
    }

    protected void setCurrencyMap(List<Currency> currencies) {
        this.currencyMap.clear();
        currencies.forEach(c -> this.currencyMap.put(c.getName(), c));
    }

    protected void setSelectedCurrency(String name) {
        this.convertSupport.setCurrency(this.currencyMap.get(name));
        System.out.println("***** Currency set to: " + name);
    }
}
