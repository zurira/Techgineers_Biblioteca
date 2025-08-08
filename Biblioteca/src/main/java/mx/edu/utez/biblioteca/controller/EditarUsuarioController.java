package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.time.LocalDate; // Importa LocalDate


public class EditarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;

    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());

        if (usuario.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
        } else {
            dpFechaNacimiento.setValue(null); // O un valor por defecto si no hay fecha
        }
    }

    @FXML
    private void onGuardar() {
        // Validaciones
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty() ||
                txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Todos los campos son requeridos.");
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
        usuarioEditando.setFechaNacimiento(dpFechaNacimiento.getValue());

        try {
            UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();
            dao.update(usuarioEditando);
            guardado = true;
            mostrarAlerta("Usuario actualizado", "Los datos se guardaron correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el usuario: " + e.getMessage());
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
    public void seleccionarFoto(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de usuario");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnSeleccionarImagen.setText(archivoSeleccionado.getName());
        } else {
            btnSeleccionarImagen.setText("Sin archivo seleccionado");
        }
    }

    @FXML
    public void cancelar(ActionEvent e) {
        cerrarVentana();
    }
}