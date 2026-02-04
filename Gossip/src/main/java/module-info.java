module com.example.mesajlasmauygulamasi {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.graphics;
    requires java.desktop;
    opens com.example.mesajlasmauygulamasi to javafx.fxml, com.google.gson;
    exports com.example.mesajlasmauygulamasi;
}