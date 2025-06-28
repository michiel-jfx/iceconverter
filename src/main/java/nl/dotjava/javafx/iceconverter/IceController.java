package nl.dotjava.javafx.iceconverter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML private TextField textfieldInput;
    @FXML private Label labelUpperRight;
    @FXML private Label labelBelowLeft;
    @FXML private ImageView fromSymbol;
    @FXML private ImageView fromFlag;
    @FXML private ImageView toSymbol;
    @FXML private ImageView toFlag;

    private static final String FLAG_SCENE = "cur-selector";
    private static final String SYMBOL_FOLDER = "symbol/circle";
    private static final String FLAGS_FOLDER  = "flag/medium";
    private final ConvertSupport convertSupport = new ConvertSupport();
    private final HashMap<String, CurrencyRate> currencyMap = new HashMap<>();
    private CurrencySetupListener currencySetupListener;
    private SceneSupport sceneSupport;

    // currency selection
    private String currentFromCurrency = "ISK";
    private String currentToCurrency = "EUR";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFlagHandlers();
    }

    public void setCurrencySetupListener(CurrencySetupListener currencySetupListener) {
        this.currencySetupListener = currencySetupListener;
    }

    public void setSceneSupport(SceneSupport sceneSupport) {
        this.sceneSupport = sceneSupport;
    }

    private void setupFlagHandlers() {
        fromSymbol.setOnMouseClicked(event -> showCurrencySelector());
        fromFlag.setOnMouseClicked(event -> showCurrencySelector());
        toSymbol.setOnMouseClicked(event -> showCurrencySelector());
        toFlag.setOnMouseClicked(event -> showCurrencySelector());
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
        updateFlagPicturesOnScene(from, toCurrency);
        convertSupport.setCurrency(customCurrency);
    }

    /**
     * Update flag and symbol to use on screen.
     * @param from Currency abbreviation to use as from
     * @param to Currency abbreviation to use as target
     */
    private void updateFlagPicturesOnScene(String from, String to) {
        Image fromSymbolImage = getCurrencyImageFromResources(SYMBOL_FOLDER, from);
        Image fromFlagImage = getCurrencyImageFromResources(FLAGS_FOLDER, from);
        Image toSymbolImage = getCurrencyImageFromResources(SYMBOL_FOLDER, to);
        Image toFlagImage = getCurrencyImageFromResources(FLAGS_FOLDER, to);
        fromSymbol.setImage(fromSymbolImage);
        fromFlag.setImage(fromFlagImage);
        toSymbol .setImage(toSymbolImage);
        toFlag.setImage(toFlagImage);
    }

    @Override
    public void onCurrencyPairSelected(String fromCurrency, String toCurrency) {
        setCurrencyToUse(fromCurrency, toCurrency);
    }
}
