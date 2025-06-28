package nl.dotjava.javafx.iceconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.dotjava.javafx.support.SceneSupport;


import static nl.dotjava.javafx.support.CurrencySupport.extractAllCurrenciesFromSite;

public class IceCo extends Application {

    private static final String MAIN_SCENE = "ice-view";
    private static final String FLAG_SCENE = "cur-selector";
    private IceController mainController;
    private SelectCurrencyController flagController;
    private SceneSupport sceneSupport;

    public void start(Stage stage) {
        System.out.println("***** Setting up stage");
        initSceneSupport(stage);
        try {
            // setup the main scene
            initMainController();
            stage.setTitle("Currency Holiday");
            stage.setScene(sceneSupport.getScene(MAIN_SCENE));

            // setup flag selection
            initFlagController();

            // setup listeners
            initListeners();

            // show the stage
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initMainController() {
        // load and setup scene from resources
        mainController = loadFxml(MAIN_SCENE);
        mainController.setSceneSupport(sceneSupport);
        mainController.setCurrencyMap(extractAllCurrenciesFromSite());
        mainController.setCurrencyToUse("ISK", "EUR");
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
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void initSceneSupport(Stage stage) {
        this.sceneSupport = new SceneSupport(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
