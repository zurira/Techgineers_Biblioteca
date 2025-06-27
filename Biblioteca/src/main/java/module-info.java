module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.biblioteca to javafx.fxml;
    exports mx.edu.utez.biblioteca;
}