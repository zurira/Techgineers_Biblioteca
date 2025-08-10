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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

    private byte[] fotografiaBytes; // Variable para guardar los bytes de la foto
    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    // Método para cargar los datos del usuario, incluyendo la foto
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

        // Cargar la foto del usuario existente
        cargarFotografia(usuario.getFotografia());
    }

    private void cargarFotografia(byte[] fotoBytes) {
        if (fotoBytes != null && fotoBytes.length > 0) {
            try {
                Image image = new Image(new ByteArrayInputStream(fotoBytes));
                imgFotoPerfil.setImage(image);
                this.fotografiaBytes = fotoBytes; // Guardar los bytes de la foto original
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
        // ... (Tu código de validación, sin cambios) ...
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

        // Asignar la foto al usuario
        usuarioEditando.setFotografia(this.fotografiaBytes);

        try {
            UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();
            dao.update(usuarioEditando); // Llamar a un método update que maneje la foto
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
            try {
                // Leer el archivo y guardar los bytes en la variable de la clase
                FileInputStream fis = new FileInputStream(selected);
                this.fotografiaBytes = new byte[(int) selected.length()];
                fis.read(this.fotografiaBytes);
                fis.close();

                // Mostrar la imagen seleccionada en la vista
                Image image = new Image(new ByteArrayInputStream(this.fotografiaBytes));
                imgFotoPerfil.setImage(image);

                btnSeleccionarImagen.setText(selected.getName());
            } catch (IOException e) {
                System.err.println("Error al cargar la imagen: " + e.getMessage());
            }
        }
    }

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