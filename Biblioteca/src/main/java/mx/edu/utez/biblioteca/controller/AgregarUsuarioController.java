package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;

public class AgregarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private Label lblFotoSeleccionada;
    @FXML private Button btnCancelar;

    private File archivoFoto;
    private UsuarioBiblioteca usuarioExistente;
    private boolean guardado = false;
    private final UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();

    public boolean isGuardado() {
        return guardado;
    }

    public void cargarDatosParaEdicion(UsuarioBiblioteca usuario) {
        this.usuarioExistente = usuario;
        txtNombre.setText(usuario.getNombre());
        dateNacimiento.setValue(usuario.getFechaNacimiento());
        txtEmail.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        lblFotoSeleccionada.setText("(foto actual)");
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar Fotografía");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );
        File selected = chooser.showOpenDialog(btnCancelar.getScene().getWindow());
        if (selected != null) {
            archivoFoto = selected;
            lblFotoSeleccionada.setText(selected.getName());
        }
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || dateNacimiento.getValue() == null ||
                txtEmail.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Campos requeridos", "Completa todos los campos marcados con *");
            return;
        }

        try {
            UsuarioBiblioteca usuario = new UsuarioBiblioteca();
            usuario.setNombre(txtNombre.getText());
            usuario.setFechaNacimiento(dateNacimiento.getValue());
            usuario.setCorreo(txtEmail.getText());
            usuario.setTelefono(txtTelefono.getText());
            usuario.setDireccion(txtDireccion.getText());
            usuario.setEstado("S"); // Puede hacerse dinámico si decides usar un ComboBox

            if (usuarioExistente == null) {
                // Nuevo usuario
                dao.create(usuario, archivoFoto);
                mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            } else {
                // Actualización
                usuario.setId(usuarioExistente.getId());
                dao.update(usuario, archivoFoto);
                mostrarAlerta("Éxito", "Usuario actualizado correctamente.");
            }

            guardado = true;
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}