package nl.dotjava.javafx.iceconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IceCo extends Application {

    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(IceCo.class.getResource("ice-view.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("IceCo");
            stage.setScene(scene);
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
