module mx.edu.utez.biblioteca {

    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;

    exports mx.edu.utez.biblioteca.controller;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    // Exporta el paquete raíz para que otras clases puedan usarse
    exports mx.edu.utez.biblioteca;

    // Abre el paquete raíz para que javafx.fxml pueda acceder a las clases si usas FXML ahí
    opens mx.edu.utez.biblioteca to javafx.fxml;

    // Abre el paquete controller para que javafx.fxml pueda acceder a los controladores
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;


    exports mx.edu.utez.biblioteca.model;
    opens mx.edu.utez.biblioteca.model to javafx.fxml;

}

