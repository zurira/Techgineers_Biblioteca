module mx.edu.utez.biblioteca {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.model; // 👈 necesario para evitar el error
    opens mx.edu.utez.biblioteca to javafx.fxml;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
    opens mx.edu.utez.biblioteca.model to javafx.fxml; // 👈 si usas FXML en el modal
}

