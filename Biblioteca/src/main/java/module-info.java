
module mx.edu.utez.biblioteca {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;

        requires org.controlsfx.controls;
        requires org.kordamp.bootstrapfx.core;
        requires org.kordamp.ikonli.javafx;
        requires org.kordamp.ikonli.fontawesome;

        // Abre el paquete principal para JavaFX
        opens mx.edu.utez.biblioteca to javafx.fxml;

        // Abre el paquete de controladores a JavaFX
        opens mx.edu.utez.biblioteca.controller to javafx.fxml;

        // Esta es la línea crucial que faltaba.
        // Abre el paquete de modelos a javafx.base para que las tablas puedan acceder a las propiedades.
        opens mx.edu.utez.biblioteca.model to javafx.base;

        exports mx.edu.utez.biblioteca;
        }
