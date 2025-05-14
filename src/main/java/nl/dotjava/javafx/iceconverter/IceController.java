package nl.dotjava.javafx.iceconverter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.support.ConvertSupport;
import nl.dotjava.javafx.support.CurrencySupport;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

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

    // temporary Icelandic conversion values
    private static final String ICELAND_NAME = "ISK";
    private static final String ICELAND_CUR = "kr ";
    private static final String ICELAND_URL = "https://sedlabanki.is/";
    private static final BigDecimal ICELAND_FROM = new BigDecimal("0.0068"); // default conversion if not found

    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);
    private ConvertSupport convertSupport;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // probably not the best place, but for now acceptable
        Currency iceland = initializeIcelandicCurrency();
        this.convertSupport = new ConvertSupport(iceland);

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
        System.out.println("***** IceController initialized");
    }

    @FXML
    protected void onIceButtonClick() {
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

    private Currency initializeIcelandicCurrency(){
        Currency iceland = new Currency(ICELAND_NAME, ICELAND_CUR);
        String rate = CurrencySupport.extractEurRate(CurrencySupport.downloadWebPageContentSynchronously(ICELAND_URL));
        if (rate != null) {
            System.out.println("***** Icelandic currency rate found: " + rate);
            iceland.setValueTo(CurrencySupport.convertRateToBigDecimal(rate));
        } else {
            // if not found (or no network connection)
            iceland.setValueFrom(ICELAND_FROM);
        }
        return iceland;
    }

    public void setPortraitModus(boolean portrait) {
        isPortrait.set(portrait);
    }
}
