package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class VerUsuarioController {

        @FXML private TextField txtNombre, txtEmail, txtTelefono;
        @FXML private DatePicker dateNacimiento;
        @FXML private TextArea txtDireccion;

        public void setDatos(String nombre, String email, String telefono, String direccion, String fecha) {
            txtNombre.setText(nombre);
            txtEmail.setText(email);
            txtTelefono.setText(telefono);
            txtDireccion.setText(direccion);
            dateNacimiento.setPromptText(fecha);
        }

        @FXML
        private void cerrar() {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();
        }
    }

