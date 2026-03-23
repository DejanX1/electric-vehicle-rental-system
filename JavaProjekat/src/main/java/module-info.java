module com.example.java_projekat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens kompanija to javafx.fxml;
    exports kompanija;

    opens kontroleri to javafx.fxml;
    exports kontroleri;

    opens iznajmljivanje to javafx.base;
    exports iznajmljivanje;

    opens vozila to javafx.base;
    exports vozila;

    exports propertiesLoader;
    exports simulacija;
    exports serialization;
    exports main;
    opens main to javafx.fxml;
}