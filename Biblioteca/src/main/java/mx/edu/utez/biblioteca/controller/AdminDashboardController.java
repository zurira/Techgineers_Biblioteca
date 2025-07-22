package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdminDashboardController {

    @FXML
    private TextField campoBusqueda;

    @FXML
    private Button btnAgregarLibro;

    @FXML
    private TableView<?> tablaLibros;

    @FXML
    public void initialize() {
        // Aquí puedes cargar la tabla con datos si ya tienes DAO o lista de libros
        btnAgregarLibro.setOnAction(e -> {
            System.out.println("Agregar libro presionado");
            // Aquí podrías abrir una nueva ventana con el formulario de agregar libro
        });
    }
}

