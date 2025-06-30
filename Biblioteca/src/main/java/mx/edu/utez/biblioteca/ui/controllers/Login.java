package mx.edu.utez.biblioteca.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.edu.utez.biblioteca.dao.UserDAO;

public class Login {

    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    
    @FXML
    private void onLogin() {
        String user = userField.getText();
        String password = passField.getText();

        UserDAO dao = new UserDAO();
        if (dao.validateUser (user, password)) {
            mostrarAlerta("Éxito", "Inicio de sesión correcto");
            // Pendiente cambiar a una vista de confirmación
        } else {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}