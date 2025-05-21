module nl.dotjava.javafx.iceconverter {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens nl.dotjava.javafx.iceconverter to javafx.fxml;
    exports nl.dotjava.javafx.iceconverter;
}
