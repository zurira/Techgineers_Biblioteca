package mx.edu.utez.biblioteca.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Login {

    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> {
            // Lógica para el inicio de sesión
            String usuario = userField.getText();
            String password = passField.getText();
            System.out.println("Intento de login con usuario: " + usuario);
            // Falta agregar validación con la base de datos.
        });

        registerLink.setOnAction(event -> {
            // Lógica para el registro de usuarios
            System.out.println("Navegar a la vista de registro");
        });
    }
}