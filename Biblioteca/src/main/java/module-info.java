module mx.edu.utez.biblioteca {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.kordamp.ikonli.javafx; // Nuevo nombre del módulo para Ikonli
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.biblioteca.model to javafx.base;

    exports mx.edu.utez.biblioteca.controller;
    // Exporta el paquete raíz para que otras clases puedan usarse
    exports mx.edu.utez.biblioteca;

    // Abre el paquete raíz para que javafx.fxml pueda acceder a las clases si usas FXML ahí
    opens mx.edu.utez.biblioteca to javafx.fxml;

    // Abre el paquete controller para que javafx.fxml pueda acceder a los controladores
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;


    opens mx.edu.utez.biblioteca.views to javafx.fxml;

    exports mx.edu.utez.biblioteca.model;
    exports mx.edu.utez.biblioteca.config;
    exports mx.edu.utez.biblioteca.dao;
    exports mx.edu.utez.biblioteca.dao.impl;

    requires org.controlsfx.controls;

}