package nl.dotjava.javafx.iceconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static nl.dotjava.javafx.support.CurrencySupport.extractAllCurrenciesFromSite;

public class IceCo extends Application {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ice-view.fxml"));
            Parent root = loader.load();
            IceController iceController = loader.getController();
            Scene scene = new Scene(root, 432.0, 855.0); // 1080 x 2340
            stage.setTitle("IceCo");
            stage.setScene(scene);
            // set initial orientation and add listeners for orientation changes
            iceController.setPortraitModus(stage.getHeight() > stage.getWidth());
            scene.widthProperty().addListener((obs, oldVal, newVal) -> iceController.setPortraitModus(scene.getHeight() > newVal.doubleValue()));
            scene.heightProperty().addListener((obs, oldVal, newVal) -> iceController.setPortraitModus(newVal.doubleValue() > scene.getWidth()));
            // load all currencies and set default to ISK
            iceController.setCurrencyMap(extractAllCurrenciesFromSite());
            iceController.setCurrencyToUse("ISK", "EUR");
            iceController.setupMainStage(stage, scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
