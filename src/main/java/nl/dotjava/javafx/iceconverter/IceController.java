package nl.dotjava.javafx.iceconverter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import nl.dotjava.javafx.listeners.CurrencySetupListener;
import nl.dotjava.javafx.support.ConvertSupport;
import nl.dotjava.javafx.listeners.FlagsSelectedListener;
import nl.dotjava.javafx.support.SceneSupport;

import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static nl.dotjava.javafx.support.CurrencySupport.getCurrencyImageFromResources;

public class IceController implements Initializable, FlagsSelectedListener {
    public IceController() {
        // default empty constructor
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

    private static final String FLAG_SCENE = "cur-selector";
    private final ConvertSupport convertSupport = new ConvertSupport();
    private final HashMap<String, CurrencyRate> currencyMap = new HashMap<>();
    private CurrencySetupListener currencySetupListener;
    private SceneSupport sceneSupport;

    // currency selection
    private String currentFromCurrency = "ISK";
    private String currentToCurrency = "EUR";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textfieldInput.textProperty().bindBidirectional(textfieldInputLandscape.textProperty());
        labelUpperRightLandscape.textProperty().bind(labelUpperRight.textProperty());
        labelBelowLeftLandscape.textProperty().bind(labelBelowLeft.textProperty());
        // adding tap-handler for selecting currencies
        setupFlagHandlers();
        System.out.println("***** IceController initialized");
    }

    public void bindOrientations() {
        if (sceneSupport != null) {
            // bind visibility of layouts to the orientation property
            portraitLayout.visibleProperty().bind(sceneSupport.isPortrait());
            portraitLayout.managedProperty().bind(sceneSupport.isPortrait());
            landscapeLayout.visibleProperty().bind(sceneSupport.isPortrait().not());
            landscapeLayout.managedProperty().bind(sceneSupport.isPortrait().not());
            System.out.println("***** Orientation bound (to " + sceneSupport.isPortrait().getValue() + ")");
        }
    }

    public void setCurrencySetupListener(CurrencySetupListener currencySetupListener) {
        this.currencySetupListener = currencySetupListener;
    }

    public void setSceneSupport(SceneSupport sceneSupport) {
        this.sceneSupport = sceneSupport;
    }

    private void setupFlagHandlers() {
        // portrait (accepting tapping on flags or currencies)
        portraitFromSymbol.setOnMouseClicked(event -> showCurrencySelector());
        portraitFromFlag.setOnMouseClicked(event -> showCurrencySelector());
        portraitToSymbol.setOnMouseClicked(event -> showCurrencySelector());
        portraitToFlag.setOnMouseClicked(event -> showCurrencySelector());
        // landscape
        landscapeFromSymbol.setOnMouseClicked(event -> showCurrencySelector());
        landscapeFromFlag.setOnMouseClicked(event -> showCurrencySelector());
        landscapeToSymbol.setOnMouseClicked(event -> showCurrencySelector());
        landscapeToFlag.setOnMouseClicked(event -> showCurrencySelector());
    }


    /**
     * Show the flag selection screen. Refresh the scene because Gluon tends to show a black screen when it is shown the
     * first time. See {@link SelectCurrencyController} for the selecting part.
     */
    private void showCurrencySelector() {
        currencySetupListener.onSetCurrencies(this.currentFromCurrency, this.currentToCurrency);
        sceneSupport.switchToScene(FLAG_SCENE);
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

    /** Initializes the currency map with all available currencies. */
    public void setCurrencyMap(List<CurrencyRate> currencies) {
        this.currencyMap.clear();
        currencies.forEach(c -> this.currencyMap.put(c.getName(), c));
        System.out.println("***** CurrencyRate map initialized");
    }

    /**
     * Set a custom from and to currency factor. A custom CurrencyRate is calculated using both currencies. Also update
     * flag and symbol on screen.
     * @param from currency symbol from
     * @param to target currency symbol, use EUR when {null}
     */
    public void setCurrencyToUse(String from, String... to) {
        String toCurrency = (to.length > 0) ? to[0] : "EUR";
        this.currentFromCurrency = from;
        this.currentToCurrency = toCurrency;
        Currency curFrom = Currency.valueOf(from);
        CurrencyRate curRateFrom = this.currencyMap.get(from);
        CurrencyRate curRateTo = this.currencyMap.get(toCurrency);
        CurrencyRate customCurrency = new CurrencyRate(curFrom);
        customCurrency.setTargetSymbol(curRateTo.getCurrencySymbol());
        customCurrency.setValueFrom(curRateFrom.getValueFrom().multiply(curRateTo.getValueTo(), new MathContext(10, RoundingMode.HALF_UP)));
        // update pictures onscreen
        updateFlagPictures(from, toCurrency);
        convertSupport.setCurrency(customCurrency);
    }

    /**
     * Update flag and symbol to use on screen.
     * @param from Currency abbreviation to use as from
     * @param to Currency abbreviation to use as target
     */
    private void updateFlagPictures(String from, String to) {
        Image fromSymbolImage = getCurrencyImageFromResources("symbol/circle", from);
        Image fromFlagImage = getCurrencyImageFromResources("flag/medium", from);
        Image toSymbolImage = getCurrencyImageFromResources("symbol/circle", to);
        Image toFlagImage = getCurrencyImageFromResources("flag/medium", to);
        portraitFromSymbol.setImage(fromSymbolImage);
        portraitFromFlag.setImage(fromFlagImage);
        portraitToSymbol.setImage(toSymbolImage);
        portraitToFlag.setImage(toFlagImage);
        landscapeFromSymbol.setImage(fromSymbolImage);
        landscapeFromFlag.setImage(fromFlagImage);
        landscapeToSymbol.setImage(toSymbolImage);
        landscapeToFlag.setImage(toFlagImage);
        System.out.println("***** IceController: Images updated for " + from + " -> " + to);
    }

    @Override
    public void onCurrencyPairSelected(String fromCurrency, String toCurrency) {
        System.out.println("***** Currency pair selected: " + fromCurrency + " -> " + toCurrency);
        setCurrencyToUse(fromCurrency, toCurrency);
    }
}
