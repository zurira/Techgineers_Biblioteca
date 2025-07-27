module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;

    opens mx.edu.utez.biblioteca to javafx.fxml;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;

    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;
    opens mx.edu.utez.biblioteca.model to javafx.base;
}