module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome; // Nuevo nombre del módulo para Ikonli

    opens mx.edu.utez.biblioteca to javafx.fxml;
    exports mx.edu.utez.biblioteca;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
}