package nl.dotjava.javafx.iceconverter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static nl.dotjava.javafx.support.ConvertSupport.convertToEuroCurrency;
import static nl.dotjava.javafx.support.ConvertSupport.convertToOtherCurrency;

public class IceController implements Initializable {

    public IceController() {
        System.out.println("IceController instantiated successfully!");
    }

    // portrait
    @FXML private TextField textfieldInput;
    @FXML private Label labelUpperRight;
    @FXML private Label labelBelowLeft;
    @FXML private Label dummyOne;
    @FXML private Label dummyTwo;
    @FXML private Label dummyThr;
    // landscape
    @FXML private TextField textfieldInputLandscape;
    @FXML private Label labelUpperRightLandscape;
    @FXML private Label labelBelowLeftLandscape;
    @FXML private Label dummyOneLandscape;
    @FXML private Label dummyTwoLandscape;
    @FXML private Label dummyThrLandscape;
    // layout containers
    @FXML private VBox portraitLayout;
    @FXML private VBox landscapeLayout;

    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);

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
        dummyOneLandscape.textProperty().bind(dummyOne.textProperty());
        dummyTwoLandscape.textProperty().bind(dummyTwo.textProperty());
        dummyThrLandscape.textProperty().bind(dummyThr.textProperty());
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
    }

    public void setPortraitModus(boolean portrait) {
        isPortrait.set(portrait);
    }
}
