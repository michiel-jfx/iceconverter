package nl.dotjava.javafx.iceconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static nl.dotjava.javafx.support.CurrencySupport.getIcalandicCurrency;
import static nl.dotjava.javafx.support.StorageSupport.loadCustomFont;

public class IceCo extends Application {

    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ice-view.fxml"));
            Parent root = loader.load();
            IceController controller = loader.getController();
            Scene scene = new Scene(root, 432.0, 855.0);
            stage.setTitle("IceCo");
            stage.setScene(scene);
            setCustomFont(root);
            // load and set Icelandic currency
            controller.setSelectedCurrency(getIcalandicCurrency());
            System.out.println("***** About to show stage");
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // set custom font for all keypad buttons
    private void setCustomFont(Parent root) {
        Font customFont = loadCustomFont();
        root.lookupAll(".key-button").forEach(node -> {
            if (node instanceof Button button) {
                button.setFont(customFont);
                button.setStyle(button.getStyle() + "; -fx-font-family: '" + customFont.getFamily() + "'; -fx-font-size: " + customFont.getSize() + "px;");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
