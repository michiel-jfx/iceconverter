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
            IceController controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setTitle("IceCo");
            stage.setScene(scene);
            // set initial orientation
            controller.setPortraitModus(stage.getHeight() > stage.getWidth());
            // add listeners for orientation changes
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                controller.setPortraitModus(scene.getHeight() > newVal.doubleValue());
            });
            scene.heightProperty().addListener((obs, oldVal, newVal) -> {
                controller.setPortraitModus(newVal.doubleValue() > scene.getWidth());
            });
            // load all currencies and set default to ISK
            controller.setCurrencyMap(extractAllCurrenciesFromSite());
            controller.setSelectedCurrency("ISK");
            System.out.println("***** About to show stage");
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
