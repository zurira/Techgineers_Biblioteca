package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class SuperAdminController {

    @FXML
    private void registrarAdministrador() {
        mostrarAlerta("Abrir ventana para registrar un nuevo administrador.");
        // Aquí se carga las  ventanas emergentes para registrar administrador
    }

    @FXML
    private void editarAdministrador() {
        mostrarAlerta("Abrir ventana para editar administrador existente.");
    }

    @FXML
    private void eliminarAdministrador() {
        mostrarAlerta("Abrir ventana para eliminar administrador.");
    }

    @FXML
    private void cerrarSesion() {
        mostrarAlerta("Cerrar sesión y volver al login.");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Acción del SuperAdmin");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}