module mx.edu.utez.biblioteca {

    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx; // Nuevo nombre del módulo para Ikonli


    exports mx.edu.utez.biblioteca.controller;

    // Exporta el paquete raíz para que otras clases puedan usarse
    exports mx.edu.utez.biblioteca;

    // Abre el paquete raíz para que javafx.fxml pueda acceder a las clases si usas FXML ahí
    opens mx.edu.utez.biblioteca to javafx.fxml;

}