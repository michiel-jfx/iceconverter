package nl.dotjava.javafx.iceconverter;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.dotjava.javafx.domain.Currency;
import nl.dotjava.javafx.domain.CurrencyRate;
import nl.dotjava.javafx.support.ConvertSupport;
import nl.dotjava.javafx.support.StageSupport;

import java.io.IOException;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static nl.dotjava.javafx.support.CurrencySupport.getCurrencyImageFromResources;

public class IceController implements Initializable, SelectCurrencyController.SelectCurrencyListener {
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

    private final SimpleBooleanProperty isPortrait = new SimpleBooleanProperty(true);
    private final ConvertSupport convertSupport = new ConvertSupport();
    private final HashMap<String, CurrencyRate> currencyMap = new HashMap<>();
    private StageSupport stageSupport;

    // currency selection
    private SelectCurrencyController selectCurrencyController;
    private Scene flagSelectScene;
    private String currentFromCurrency = "ISK";
    private String currentToCurrency = "EUR";

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
        // adding tap-handler for selecting currencies
        setupFlagHandlers();

        System.out.println("***** IceController initialized");
    }

    /** remember the main stage and scene (and their size). */
    public void setupMainStage(Stage stage, Scene scene) {
        stageSupport = new StageSupport(stage, scene);
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

    /** Load the Currency Selector scene and sets a listener to this controller. */
    private void initializeCurrencySelector() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cur-selector.fxml"));
            Parent root = loader.load();
            selectCurrencyController = loader.getController();
            selectCurrencyController.setSelectCurrencyListener(this);
            flagSelectScene = new Scene(root, stageSupport.getWidth(), stageSupport.getHeight());
            System.out.println("***** flagScene size after initialization: " + flagSelectScene.getWidth() + " x " + flagSelectScene.getHeight());
            System.out.println("***** Currency Selector scene initialized");
        } catch (IOException e) {
            System.err.println("Error loading currency selector during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show the flag selection screen. Refresh the scene because Gluon tends to show a black screen when it is shown the
     * first time. See {@link SelectCurrencyController} for the selecting part.
     */
    private void showCurrencySelector() {
        try {
            if (flagSelectScene == null) {
                // create the scene on first use, cannot be done earlier due to refresh issues
                initializeCurrencySelector();
            }
            selectCurrencyController.setCurrentCurrencies(this.currentFromCurrency, this.currentToCurrency);
            stageSupport.switchToScene(flagSelectScene);
        } catch (Exception e) {
            System.err.println("Error showing currency selector: " + e.getMessage());
            e.printStackTrace();
        }
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

    /** Called from main application during initialization and listener. */
    public void setPortraitModus(boolean portrait) {
        isPortrait.set(portrait);
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
        System.out.println("***** IceController: Currency pair selected: " + fromCurrency + " -> " + toCurrency);
        setCurrencyToUse(fromCurrency, toCurrency);
        onBackToMain();
    }

    @Override
    public void onBackToMain() {
        System.out.println("***** onBackToMain() called");
        stageSupport.switchBackToMainScene();
    }
}
