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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class AgregarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;
    @FXML private ImageView imgFotoPerfil;

    private File archivoFoto;
    private boolean guardado = false;
    private final UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();

    public boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );
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
    public void guardar(ActionEvent actionEvent) {
        if (!validarCampos()) {
            return; // Detiene la ejecución si la validación falla
        }

        byte[] fotoBytes = null;
        try {
            if (this.archivoFoto != null) {
                fotoBytes = Files.readAllBytes(this.archivoFoto.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar la imagen", "Ocurrió un error al intentar leer el archivo de la imagen.");
            return;
        }

        try {
            // Validar que la foto no sea nula
            if (fotoBytes == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campo obligatorio", "Debe seleccionar una fotografía para el usuario.");
                return;
            }

            // Validar que el nombre de usuario no esté repetido
            if (dao.existeNombre(txtNombre.getText().trim())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Nombre repetido", "El nombre de usuario '" + txtNombre.getText().trim() + "' ya existe. Por favor, ingrese uno diferente.");
                return;
            }

            UsuarioBiblioteca usuario = new UsuarioBiblioteca();
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setFechaNacimiento(dpFechaNacimiento.getValue());
            usuario.setCorreo(txtCorreo.getText().trim());
            usuario.setTelefono(txtTelefono.getText().trim());
            usuario.setDireccion(txtDireccion.getText().trim());
            usuario.setEstado("Activo");
            usuario.setFotografia(fotoBytes);

            boolean resultado = dao.create(usuario);

            if (resultado) {
                guardado = true;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Operación exitosa", "El usuario se ha creado correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Operación fallida", "No se pudo guardar el usuario en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", "Ocurrió un error al interactuar con la base de datos", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                dpFechaNacimiento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, complete todos los campos obligatorios.");
            return false;
        }
        return true;
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