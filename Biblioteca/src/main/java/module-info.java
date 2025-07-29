module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires java.desktop;

    // Indica qué paquetes se abren para reflexión a javafx.fxml
    opens mx.edu.utez.biblioteca to javafx.fxml;
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;

    // Exporta paquetes públicos si deseas usarlos desde otros módulos
    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;

    // Abre el paquete model si haces bindings o reflexión desde javafx.base
    opens mx.edu.utez.biblioteca.model to javafx.base;
}