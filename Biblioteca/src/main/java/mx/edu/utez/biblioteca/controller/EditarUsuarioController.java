package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.time.LocalDate;

public class EditarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;
    @FXML private ImageView imgFotoPerfil;

    private File archivoFoto; // <-- Usamos File, no byte[]
    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    // Ya no necesitas un método cargarFotografia que use byte[]
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        if (usuario.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
        } else {
            dpFechaNacimiento.setValue(null);
        }

        // Si el usuario tiene una foto guardada, hay que mostrarla
        if (usuario.getFotografia() != null) {
            try {
                // Aquí, creamos la imagen desde los bytes que el DAO aún devuelve
                Image image = new Image(new java.io.ByteArrayInputStream(usuario.getFotografia()));
                imgFotoPerfil.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                imgFotoPerfil.setImage(new Image(getClass().getResourceAsStream("/mx/edu/utez/biblioteca/img/placeholder.png")));
            }
        } else {
            imgFotoPerfil.setImage(new Image(getClass().getResourceAsStream("/mx/edu/utez/biblioteca/img/placeholder.png")));
        }
    }

    @FXML
    private void onGuardar() {
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
            // Llama al método update que recibe un File
            dao.update(usuarioEditando, archivoFoto);
            guardado = true;
            mostrarAlerta("Usuario actualizado", "Los datos se guardaron correctamente.");
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar el usuario: " + e.getMessage());
        }
    }

    @FXML
    public void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de usuario");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fileChooser.showOpenDialog(btnCancelar.getScene().getWindow());
        if (selected != null) {
            this.archivoFoto = selected; //Guardamos el File seleccionado
            btnSeleccionarImagen.setText(selected.getName());
            try {
                Image image = new Image(selected.toURI().toString());
                imgFotoPerfil.setImage(image);
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen: " + e.getMessage());
            }
        }
    }

    // ... (El resto de tus métodos auxiliares) ...
    public boolean isGuardado() { return guardado; }
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
    @FXML public void cancelar(ActionEvent e) { cerrarVentana(); }
}