package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional; // Se añade el import si no está

public class EditarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;
    @FXML private ImageView imgFotoPerfil;

    private File archivoFoto;
    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;
    private final UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();

    public boolean isGuardado() {
        return guardado;
    }

    // Método para cargar los datos del usuario en la vista al iniciarla
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        if (usuario.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
        }

        // Carga la foto del usuario desde el objeto 'usuario' si existe
        byte[] fotoBytes = usuario.getFotografia();
        if (fotoBytes != null && fotoBytes.length > 0) {
            try {
                Image userImage = new Image(new java.io.ByteArrayInputStream(fotoBytes));
                imgFotoPerfil.setImage(userImage);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error al cargar la fotografía del usuario. La imagen se mostrará en blanco.");
            }
        }
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                this.archivoFoto = selectedFile;
                Image image = new Image(new FileInputStream(this.archivoFoto));
                this.imgFotoPerfil.setImage(image);
                System.out.println("Imagen seleccionada: " + selectedFile.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen", "El archivo de imagen no se encontró.");
            }
        }
    }

    @FXML
    private void onGuardar() {
        // Validación de campos obligatorios
        if (txtNombre.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() || txtDireccion.getText().trim().isEmpty() ||
                dpFechaNacimiento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", "Campos incompletos", "Por favor, complete todos los campos.");
            return;
        }

        String nuevoNombre = txtNombre.getText().trim();
        String nombreActual = usuarioEditando.getNombre().trim();

        try {
            // Validar que el nuevo nombre no se repita, excepto si es el nombre actual
            if (!nuevoNombre.equalsIgnoreCase(nombreActual) && dao.existeNombre(nuevoNombre)) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Nombre repetido", "El nombre de usuario '" + nuevoNombre + "' ya existe. Por favor, ingrese uno diferente.");
                return;
            }

            usuarioEditando.setNombre(nuevoNombre);
            usuarioEditando.setCorreo(txtCorreo.getText().trim());
            usuarioEditando.setTelefono(txtTelefono.getText().trim());
            usuarioEditando.setDireccion(txtDireccion.getText().trim());
            usuarioEditando.setFechaNacimiento(dpFechaNacimiento.getValue());

            // Lógica para manejar la foto (si se seleccionó una nueva)
            if (this.archivoFoto != null) {
                try {
                    byte[] fotoBytes = Files.readAllBytes(this.archivoFoto.toPath());
                    usuarioEditando.setFotografia(fotoBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de archivo", "Error de actualización", "No se pudo leer la nueva fotografía.");
                    return;
                }
            }

            boolean resultado = dao.update(usuarioEditando);
            if (resultado) {
                guardado = true;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Operación exitosa", "Los datos del usuario se guardaron correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Operación fallida", "No se pudo actualizar el usuario en la base de datos.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de actualización", "Ocurrió un error al actualizar el usuario: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar(ActionEvent e) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}