package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private TextField campoBusqueda;

    @FXML
    private TableView<?> tablaLibros;

    @FXML
    private Label nombreUsuario;

    @FXML
    public void initialize() {
        // Lógica inicial si la necesitas
        btnCerrarSesion.setOnAction(event -> mostrarConfirmacionCerrarSesion());
    }

    private void mostrarConfirmacionCerrarSesion() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar cierre de sesión");
        alerta.setHeaderText("¿Seguro que quieres cerrar sesión?");
        alerta.showAndWait();
    }
}
