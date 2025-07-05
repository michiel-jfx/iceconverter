module nl.dotjava.javafx.holidayconverter {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.gluonhq.attach.storage;

    opens nl.dotjava.javafx.holidayconverter to javafx.fxml;
    opens nl.dotjava.javafx.domain to com.fasterxml.jackson.databind;
    exports nl.dotjava.javafx.holidayconverter;
}
