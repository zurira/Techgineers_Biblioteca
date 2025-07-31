module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;

    requires java.sql;
    requires java.desktop;

    opens mx.edu.utez.biblioteca to javafx.fxml;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
    opens mx.edu.utez.biblioteca.model to javafx.base; // IMPRESCINDIBLE para TableView
    opens mx.edu.utez.biblioteca.views to javafx.fxml;
    opens mx.edu.utez.biblioteca.css to javafx.fxml;

    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;
    exports mx.edu.utez.biblioteca.model;
}