package nl.dotjava.javafx.iceconverter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import nl.dotjava.javafx.support.ConvertSupport;

import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class IceController implements Initializable {

    public IceController() {
        System.out.println("***** IceController instantiated");
    }

    // portrait
    @FXML private TextField textfieldInput;
    @FXML private Label labelUpperRight;
    @FXML private Label labelBelowLeft;
    @FXML private ImageView portraitFromSymbol;
    @FXML private ImageView portraitFromFlag;
    @FXML private ImageView portraitToSymbol;
    @FXML private ImageView portraitToFlag;
    // landscape
    @FXML private TextField textfieldInputLandscape;
    @FXML private Label labelUpperRightLandscape;
    @FXML private Label labelBelowLeftLandscape;
    @FXML private ImageView landscapeFromSymbol;
    @FXML private ImageView landscapeFromFlag;
    @FXML private ImageView landscapeToSymbol;
    @FXML private ImageView landscapeToFlag;
    // layout containers
    @FXML private VBox portraitLayout;
    @FXML private VBox landscapeLayout;

    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);
    private final ConvertSupport convertSupport = new ConvertSupport();
    private final HashMap<String, CurrencyRate> currencyMap = new HashMap<>();

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

    public void setPortraitModus(boolean portrait) {
        isPortrait.set(portrait);
    }

    public void setCurrencyMap(List<CurrencyRate> currencies) {
        this.currencyMap.clear();
        currencies.forEach(c -> this.currencyMap.put(c.getName(), c));
        System.out.println("***** CurrencyRate map initialized");
    }

    /**
     * Set a custom from and to currency factor. A custom CurrencyRate is calculated using both currencies.
     * @param from currency symbol from
     * @param to target currency symbol, use EUR when {null}
     */
    public void setCurrencyToUse(String from, String... to) {
        String toCurrency = (to.length > 0) ? to[0] : "EUR";
        Currency curFrom = Currency.valueOf(from);
        CurrencyRate curRateFrom = this.currencyMap.get(from);
        CurrencyRate curRateTo = this.currencyMap.get(toCurrency);
        CurrencyRate customCurrency = new CurrencyRate(curFrom);
        customCurrency.setTargetSymbol(curRateTo.getCurrencySymbol());
        customCurrency.setValueFrom(curRateFrom.getValueFrom().multiply(curRateTo.getValueTo(), new MathContext(10, RoundingMode.HALF_UP)));
        // update pictures onscreen
        updateFlagPictures(from, toCurrency);
        this.convertSupport.setCurrency(customCurrency);
    }

    /**
     * Update flag and symbols on screen.
     */
    private void updateFlagPictures(String from, String to) {
        Image fromSymbolImage = getImageFromResource("symbol/circle", from);
        Image fromFlagImage = getImageFromResource("flag/medium", from);
        Image toSymbolImage = getImageFromResource("symbol/circle", to);
        Image toFlagImage = getImageFromResource("flag/medium", to);
        portraitFromSymbol.setImage(fromSymbolImage);
        portraitFromFlag.setImage(fromFlagImage);
        portraitToSymbol.setImage(toSymbolImage);
        portraitToFlag.setImage(toFlagImage);
        landscapeFromSymbol.setImage(fromSymbolImage);
        landscapeFromFlag.setImage(fromFlagImage);
        landscapeToSymbol.setImage(toSymbolImage);
        landscapeToFlag.setImage(toFlagImage);
        System.out.println("***** Images updated for " + from + " -> " + to);
    }

    private Image getImageFromResource(String folder, String resource) {
        try {
            return new Image(getClass().getResourceAsStream(folder + "/" + resource + ".png"));
        } catch (Exception e) {
            System.err.println("Error loading image (" + resource + "): " + e.getMessage());
        }
        return null;
    }
}
