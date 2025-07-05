package nl.dotjava.javafx.holidayconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.dotjava.javafx.support.SceneSupport;


import static nl.dotjava.javafx.support.CurrencySupport.extractAllCurrenciesFromSite;
import static nl.dotjava.javafx.support.StorageSupport.loadUsedCurrencies;

public class HoliCur extends Application {

    private static final String MAIN_SCENE = "holiday-currency-converter";
    private static final String FLAG_SCENE = "currency-selector";
    private HoliCurController mainController;
    private SelectCurrencyController flagController;
    private SceneSupport sceneSupport;

    public void start(Stage stage) {
        this.sceneSupport = new SceneSupport(stage);

        // setup the main scene
        initMainController();
        stage.setTitle("Holiday Currency Converter");
        stage.setScene(sceneSupport.getScene(MAIN_SCENE));

        // setup flag selection scene
        initFlagController();

        // setup listeners
        initListeners();

        // show the stage
        stage.show();
    }

    private void initMainController() {
        // load and setup scene from resources
        mainController = loadFxml(MAIN_SCENE);
        mainController.setSceneSupport(sceneSupport);
        mainController.setCurrencyMap(extractAllCurrenciesFromSite());
        String[] currenciesUsed = loadUsedCurrencies();
        mainController.setCurrencyToUse(currenciesUsed[0], currenciesUsed[1]);
    }

    private void initFlagController() {
        flagController = loadFxml(FLAG_SCENE);
        flagController.setSceneSupport(sceneSupport);
    }

    private void initListeners() {
        if (flagController != null && mainController != null) {
            flagController.setFlagSelectListener(mainController);
            mainController.setCurrencySetupListener(flagController);
            System.out.println("***** Listeners initialized");
        }
    }

    private <T> T loadFxml(String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(name + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root); //  432.0 x 855.0 and dimensions 1080 x 2340
            this.sceneSupport.addScene(name, scene);
            System.out.println("***** FXML loaded: " + name);
            return loader.getController();
        } catch (Exception e) {
            System.err.println("***** Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
