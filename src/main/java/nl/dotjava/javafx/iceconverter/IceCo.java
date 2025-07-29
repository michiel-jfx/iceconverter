package nl.dotjava.javafx.iceconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static nl.dotjava.javafx.support.CurrencySupport.getIcalandicCurrency;

public class IceCo extends Application {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ice-view.fxml"));
            Parent root = loader.load();
            IceController controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setTitle("IceCo");
            stage.setScene(scene);
            // load and set Icelandic currency
            controller.setSelectedCurrency(getIcalandicCurrency());
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
