package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import mx.edu.utez.biblioteca.dao.impl.AutorDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.CategoriaDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.EditorialDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.model.Libro;

import java.util.List;

public class LibroFormController {

    @FXML
    private TextField txtTitulo, txtIsbn, txtSinopsis, txtAnio, txtPortada;

    @FXML
    private ComboBox<Autor> cmbAutor;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private ComboBox<Editorial> cmbEditorial;

    @FXML
    private ComboBox<String> cmbEstado;

    @FXML
    private ImageView imgPreview;

    @FXML
    private Button btnGuardar;

    private final AutorDaoImpl autorDao = new AutorDaoImpl();
    private final CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();
    private final EditorialDaoImpl editorialDao = new EditorialDaoImpl();
    private final LibroDaoImpl libroDao = new LibroDaoImpl();

    @FXML
    public void initialize() {
        try {
            cmbEstado.getItems().addAll("ACTIVO", "INACTIVO");

            cmbAutor.getItems().addAll(autorDao.findAll());
            cmbCategoria.getItems().addAll(categoriaDao.findAll());
            cmbEditorial.getItems().addAll(editorialDao.findAll());

            txtPortada.textProperty().addListener((obs, oldVal, newVal) -> cargarImagen(newVal));

        } catch (Exception e) {
            mostrarAlerta("❌ Error al inicializar el formulario:\n" + e.getMessage());
        }
    }

    private void cargarImagen(String url) {
        try {
            if (url != null && !url.trim().isEmpty()) {
                imgPreview.setImage(new Image(url, true));
            } else {
                imgPreview.setImage(null);
            }
        } catch (Exception e) {
            imgPreview.setImage(null);
        }
    }

    @FXML
    public void handleGuardar(ActionEvent event) {
        try {
            if (validarCampos()) {
                Libro libro = new Libro();
                libro.setTitulo(txtTitulo.getText().trim());
                libro.setIsbn(txtIsbn.getText().trim());
                libro.setResumen(txtSinopsis.getText().trim());
                libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
                libro.setPortada(txtPortada.getText().trim());
                libro.setAutor(cmbAutor.getValue());
                libro.setCategoria(cmbCategoria.getValue());
                libro.setEditorial(cmbEditorial.getValue());
                libro.setEstado(cmbEstado.getValue());

                libroDao.create(libro);
                mostrarAlerta("✅ Libro guardado exitosamente.");
                limpiarFormulario();
            } else {
                mostrarAlerta("⚠️ Completa todos los campos antes de guardar.");
            }
        } catch (NumberFormatException nfe) {
            mostrarAlerta("⚠️ Año de publicación debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("❌ Error al guardar el libro:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        return !txtTitulo.getText().isEmpty() &&
                !txtIsbn.getText().isEmpty() &&
                !txtSinopsis.getText().isEmpty() &&
                !txtAnio.getText().isEmpty() &&
                !txtPortada.getText().isEmpty() &&
                cmbAutor.getValue() != null &&
                cmbCategoria.getValue() != null &&
                cmbEditorial.getValue() != null &&
                cmbEstado.getValue() != null;
    }

    private void limpiarFormulario() {
        txtTitulo.clear();
        txtIsbn.clear();
        txtSinopsis.clear();
        txtAnio.clear();
        txtPortada.clear();
        cmbAutor.getSelectionModel().clearSelection();
        cmbCategoria.getSelectionModel().clearSelection();
        cmbEditorial.getSelectionModel().clearSelection();
        cmbEstado.getSelectionModel().clearSelection();
        imgPreview.setImage(null);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📚 Biblioteca");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}