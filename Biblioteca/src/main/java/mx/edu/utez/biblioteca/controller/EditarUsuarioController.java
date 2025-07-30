package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;

public class EditarUsuarioController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ToggleButton toggleEstado;
    @FXML
    private Label lblFotoSeleccionada;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        toggleEstado.setSelected("S".equalsIgnoreCase(usuario.getEstado()));
        actualizarTextoToggle();

        toggleEstado.selectedProperty().addListener((obs, oldVal, newVal) -> actualizarTextoToggle());
    }

    private void actualizarTextoToggle() {
        toggleEstado.setText(toggleEstado.isSelected() ? "Activo" : "Inactivo");
    }

    @FXML
    private void onGuardar() {
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "El nombre y correo son requeridos.");
            return;
        }

        if (!txtCorreo.getText().contains("@")) {
            mostrarAlerta("Correo inválido", "Introduce un correo válido.");
            return;
        }

        usuarioEditando.setNombre(txtNombre.getText().trim());
        usuarioEditando.setCorreo(txtCorreo.getText().trim());
        usuarioEditando.setTelefono(txtTelefono.getText().trim());
        usuarioEditando.setDireccion(txtDireccion.getText().trim());
        usuarioEditando.setEstado(toggleEstado.isSelected() ? "S" : "N");

        try {
            UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();
            dao.update(usuarioEditando);
            guardado = true;
            mostrarAlerta("Usuario actualizado", "Los datos se guardaron correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el usuario.");
        }
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void seleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de usuario");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(lblFotoSeleccionada.getScene().getWindow());
        if (archivoSeleccionado != null) {
            lblFotoSeleccionada.setText(archivoSeleccionado.getName());
        } else {
            lblFotoSeleccionada.setText("Sin archivo seleccionado");
        }
    }

        @FXML
        public void cancelar(ActionEvent event) {
            // Cierra el modal actual
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }


    @FXML
    public void cambiarEstado(ActionEvent event) {
        if (toggleEstado.isSelected()) {
            toggleEstado.setText("Activo");
            toggleEstado.setStyle("-fx-background-color: #807d59; -fx-text-fill: white;");
        } else {
            toggleEstado.setText("Inactivo");
            toggleEstado.setStyle("-fx-background-color: #9b9593; -fx-text-fill: white;");
        }
    }
}

