module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx; // Nuevo nombre del módulo para Ikonli
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.biblioteca.controller to javafx.fxml;
    opens mx.edu.utez.biblioteca.views to javafx.fxml;
    opens mx.edu.utez.biblioteca.model to javafx.base;


    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;
    exports mx.edu.utez.biblioteca.model;
    exports mx.edu.utez.biblioteca.config;
    exports mx.edu.utez.biblioteca.dao;
    exports mx.edu.utez.biblioteca.dao.impl;

}