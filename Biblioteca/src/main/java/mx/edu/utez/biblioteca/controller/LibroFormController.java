package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*;
import mx.edu.utez.biblioteca.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;

public class LibroFormController {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtEditorial;
    @FXML private TextArea txtSinopsis;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtAnioPublicacion;
    @FXML private TextField txtEstado;

    @FXML private ImageView imageView;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Variable para almacenar el archivo de la imagen seleccionada
    private File imagenSeleccionada;
    private boolean agregado = false;

    public boolean seAgregoLibro() {
        return agregado;
    }

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(event -> guardarLibro());
        btnCancelar.setOnAction(event -> cerrarModal());
        btnSeleccionarImagen.setOnAction(event -> seleccionarImagen());
    }

    /**
     * Permite al usuario seleccionar un archivo de imagen para la portada del libro.
     */
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Establecer filtros para tipos de archivo
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Imagen", Arrays.asList("*.png", "*.jpg", "*.jpeg"));
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                imagenSeleccionada = selectedFile;
                Image image = new Image(new FileInputStream(imagenSeleccionada));
                imageView.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen", "El archivo de imagen no se encontró.");
            }
        }
    }

    @FXML
    private void guardarLibro() {
        // Validación de campos
        if (txtIsbn.getText().trim().isEmpty() ||
                txtTitulo.getText().trim().isEmpty() ||
                txtAutor.getText().trim().isEmpty() ||
                txtEditorial.getText().trim().isEmpty() ||
                txtSinopsis.getText().trim().isEmpty() ||
                txtCategoria.getText().trim().isEmpty() ||
                txtAnioPublicacion.getText().trim().isEmpty() ||
                txtEstado.getText().trim().isEmpty() ||
                imagenSeleccionada == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos del formulario, incluyendo la selección de una foto.");
            return;
        }

        try {
            Autor autor = new Autor(0, txtAutor.getText());
            Editorial editorial = new Editorial(0, txtEditorial.getText());
            Categoria categoria = new Categoria(0, txtCategoria.getText());

            Libro nuevoLibro = new Libro();
            nuevoLibro.setIsbn(txtIsbn.getText());
            nuevoLibro.setTitulo(txtTitulo.getText());
            nuevoLibro.setAutor(autor);
            nuevoLibro.setEditorial(editorial);
            nuevoLibro.setResumen(txtSinopsis.getText());
            nuevoLibro.setCategoria(categoria);
            nuevoLibro.setAnioPublicacion(Integer.parseInt(txtAnioPublicacion.getText()));
            nuevoLibro.setEstado(txtEstado.getText());

            byte[] fotoBytes = null;
            if (imagenSeleccionada != null) {
                fotoBytes = Files.readAllBytes(imagenSeleccionada.toPath());
                nuevoLibro.setPortada(Base64.getEncoder().encodeToString(fotoBytes));
            }

            // Aquí se usaría el dao para guardar el libro
            LibroDaoImpl libroDao = new LibroDaoImpl();
            libroDao.create(nuevoLibro);

            agregado = true;
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Libro registrado exitosamente!");
            cerrarModal();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido", "El año de publicación debe ser un número.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al registrar el libro.", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}
