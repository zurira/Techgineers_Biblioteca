module mx.edu.utez.biblioteca {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
    opens mx.edu.utez.biblioteca to javafx.graphics;

    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;

}
