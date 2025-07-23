module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

<<<<<<< HEAD
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
=======
    opens mx.edu.utez.biblioteca to javafx.graphics;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
>>>>>>> TTS04

    exports mx.edu.utez.biblioteca;
<<<<<<< HEAD
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
=======
    exports mx.edu.utez.biblioteca.controller;
>>>>>>> TTS04
}