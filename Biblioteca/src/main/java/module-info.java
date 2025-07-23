module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.biblioteca to javafx.graphics;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;

    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;
}