package nl.dotjava.javafx.iceconverter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static nl.dotjava.javafx.support.ConvertSupport.convertToEuroCurrency;
import static nl.dotjava.javafx.support.ConvertSupport.convertToOtherCurrency;

public class IceController {
    public IceController() {
        System.out.println("IceController instantiated successfully!");
    }

    @FXML
    private Label welcomeText;
    @FXML
    private Label labelBelowLeft;
    @FXML
    private Label labelUpperRight;
    @FXML
    private TextField textfieldInput;

    @FXML
    public void initialize() {
        System.out.println("IceController initialize method called!");

        if (textfieldInput == null) {
            System.out.println("Error: textfieldInput is null! Check fx:id in FXML!");
        } else {
            textfieldInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) { // lost focus
                    onIceButtonClick();
                }
            });
        }
    }

    @FXML
    protected void onIceButtonClick() {
        if (textfieldInput != null) {
            String inputText = textfieldInput.getText();
            try {
                labelUpperRight.setText(convertToOtherCurrency(inputText));
                labelBelowLeft.setText(convertToEuroCurrency(inputText));
            } catch (Exception e) {
                System.err.println("Error during conversion: " + e.getMessage());
            }
        }
        welcomeText.setText("llt í lagi");
    }
}
