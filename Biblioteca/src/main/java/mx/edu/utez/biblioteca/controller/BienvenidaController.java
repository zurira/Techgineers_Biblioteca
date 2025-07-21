package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;

public class BienvenidaController {
    LibroDaoImpl libroDao = new LibroDaoImpl();

    @FXML private FlowPane contenedorLibros;


}

