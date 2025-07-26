package mx.edu.utez.biblioteca.controller;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Usuario;

public class AgregarUsuarioController {

        @FXML private TextField txtNombre;
        @FXML private TextField txtFecha;
        @FXML private TextField txtEmail;
        @FXML private TextField txtTelefono;
        @FXML private TextArea txtDireccion;

        private Usuario usuario;

        private boolean guardado = false;

        public Usuario getUsuario() {
            return usuario;
        }

        public boolean isGuardado() {
            return guardado;
        }

        @FXML
        private void cancelar() {
            ((Stage) txtNombre.getScene().getWindow()).close();
        }

        @FXML
        private void guardar() {
            if (txtNombre.getText().isEmpty() || txtFecha.getText().isEmpty() ||
                    txtEmail.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Campos obligatorios");
                alerta.setHeaderText(null);
                alerta.setContentText("Por favor, completa todos los campos marcados con *.");
                alerta.showAndWait();
                return;
            }

            usuario = new Usuario(
                    txtNombre.getText(),
                    txtFecha.getText(),
                    txtEmail.getText(),
                    txtTelefono.getText(),
                    txtDireccion.getText()
            );

            guardado = true;
            ((Stage) txtNombre.getScene().getWindow()).close();
        }
    }