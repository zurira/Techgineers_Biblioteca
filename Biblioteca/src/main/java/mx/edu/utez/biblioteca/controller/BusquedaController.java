package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;

public class BusquedaController {
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private FlowPane contenedorResultados;

    LibroDaoImpl libroDao = new LibroDaoImpl();
}
