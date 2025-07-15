package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.edu.utez.biblioteca.dao.UserDAO;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void onLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos vacíos", "Debes ingresar usuario y contraseña.");
            return;
        }

        User user = new UserDAO().validateUser(username, password);
        if (user != null) {
            showAlert(Alert.AlertType.INFORMATION, "Bienvenido", "Inicio de sesión exitoso para " + user.getUser());
            // TODO: Redirigir al dashboard u otra vista aquí
        } else {
            showAlert(Alert.AlertType.ERROR, "Credenciales incorrectas", "Usuario o contraseña inválidos.");
        }
        printAllUsers();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Quita el encabezado si no quieres uno
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void printAllUsers() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("❌ No hay conexión con la base de datos");
            return;
        }

        String sql = "SELECT ID, USERNAME, USERPASSWORD FROM USUARIOS";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("📋 Usuarios registrados:");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String username = rs.getString("USERNAME");
                String password = rs.getString("USERPASSWORD");

                System.out.println("🆔 ID: " + id + " | 👤 Usuario: " + username + " | 🔑 Contraseña: " + password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
