package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarUsuarioController {
    @FXML
    private TextField txtNombre, txtEmail, txtTelefono;
    @FXML
    private DatePicker dateNacimiento;
    @FXML
    private TextArea txtDireccion;

    @FXML
    private void guardar() {
        System.out.println("Guardado: " + txtNombre.getText());
        cerrar();
    }
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

}

