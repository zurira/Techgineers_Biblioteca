package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

public class EditarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private ToggleButton toggleEstado;
    @FXML private Button btnGuardar;

    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        toggleEstado.setSelected("S".equalsIgnoreCase(usuario.getEstado()));
        toggleEstado.setText(toggleEstado.isSelected() ? "Activo" : "Inactivo");

        toggleEstado.selectedProperty().addListener((obs, oldVal, newVal) -> {
            toggleEstado.setText(newVal ? "Activo" : "Inactivo");
        });
    }

    @FXML
    private void onGuardar() {
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "El nombre y correo son requeridos.");
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}