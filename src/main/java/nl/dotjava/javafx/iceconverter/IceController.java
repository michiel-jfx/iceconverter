package nl.dotjava.javafx.iceconverter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.support.ConvertSupport;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ESCAPE;

public class IceController implements Initializable {

    public IceController() {
        System.out.println("***** IceController instantiated");
    }

    // layout fields
    @FXML private TextField textfieldInput;
    @FXML private Label labelUpperRight;
    @FXML private Label labelBelowLeft;
    @FXML private VBox sceneLayout;

    private final ConvertSupport convertSupport = new ConvertSupport();
    private LocalDateTime lastClick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textfieldInput.setEditable(false);
        textfieldInput.setFocusTraversable(false);
        lastClick = LocalDateTime.now();
        setupBackButtonHandler();
        System.out.println("***** IceController initialized");
    }

    /** Handle tapping on any button. */
    @FXML
    protected void onButtonTap(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        String currentText = textfieldInput.getText();
        switch (buttonText) {
            case "<", "⌫", "x":
                handleBackspace(currentText);
                break;
            default:
                textfieldInput.setText(currentText + buttonText);
                break;
        }
        try {
            // immediately update currency conversion
            currentText = textfieldInput.getText();
            labelUpperRight.setText(convertSupport.convertToOtherCurrency(currentText));
            labelBelowLeft.setText(convertSupport.convertToEuroCurrency(currentText));
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
        }
    }

    /** Handle erase character tapping event. */
    private void handleBackspace(String currentText) {
        LocalDateTime now = LocalDateTime.now();
        long delta = Duration.between(lastClick, now).toMillis();
        if (!currentText.isEmpty()) {
            textfieldInput.setText((delta < 400) ? "" : currentText.substring(0, currentText.length() - 1));
        }
        lastClick = now;
    }

    /** Add handler to scene to catch the Android previous or back tapping event. */
    private void setupBackButtonHandler() {
        // ensure the scene is fully loaded before adding the key-handler
        Platform.runLater(() -> {
            if (sceneLayout.getScene() != null) {
                sceneLayout.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == ESCAPE || event.getCode() == BACK_SPACE) {
                        Platform.exit();
                        System.exit(0);
                        event.consume();
                    }
                });
            }
        });
    }

    protected void setSelectedCurrency(Currency currency) {
        this.convertSupport.setCurrency(currency);
    }
}
