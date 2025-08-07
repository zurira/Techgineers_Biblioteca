package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*;
import mx.edu.utez.biblioteca.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate; // Importación para obtener el año actual
import java.util.Optional;

public class LibroFormController {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Autor> cmbAutor;
    @FXML private ComboBox<Editorial> cmbEditorial;
    @FXML private TextArea txtSinopsis;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtAnioPublicacion;
    @FXML private TextField txtUrlPortada;

    @FXML private ImageView imageView;
    @FXML private Button btnCargarUrl;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML private Button btnAgregarAutor;
    @FXML private Button btnAgregarEditorial;
    @FXML private Button btnAgregarCategoria;

    private LibroDaoImpl libroDao = new LibroDaoImpl();
    private AutorDaoImpl autorDao = new AutorDaoImpl();
    private EditorialDaoImpl editorialDao = new EditorialDaoImpl();
    private CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();

    private Libro libroEditado;
    private boolean agregado = false;

    public boolean isAgregado() {
        return agregado;
    }

    public void setLibro(Libro libro) {
        this.libroEditado = libro;
        if (libro != null) {
            txtTitulo.setText(libro.getTitulo());
            txtIsbn.setText(libro.getIsbn());
            txtSinopsis.setText(libro.getResumen());
            txtAnioPublicacion.setText(String.valueOf(libro.getAnioPublicacion()));
            txtUrlPortada.setText(libro.getPortada());
            cmbEditorial.getSelectionModel().select(libro.getEditorial());
            cmbAutor.getSelectionModel().select(libro.getAutor());
            cmbCategoria.getSelectionModel().select(libro.getCategoria());

            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                cargarImagen(libro.getPortada());
            }
        }
    }

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(event -> guardarLibro());
        btnCancelar.setOnAction(event -> cerrarModal());
        btnCargarUrl.setOnAction(event -> cargarImagenDesdeUrl());

        // Se agrega el Tooltip para el botón de cargar imagen
        Tooltip cargarImagenTooltip = new Tooltip("Da click para cargar la imagen del libro");
        Tooltip.install(btnCargarUrl, cargarImagenTooltip);

        btnAgregarAutor.setOnAction(event -> handleAddAutor());
        btnAgregarEditorial.setOnAction(event -> handleAddEditorial());
        btnAgregarCategoria.setOnAction(event -> handleAddCategoria());

        cargarAutores();
        cargarEditoriales();
        cargarCategorias();
    }

    @FXML
    private void cargarImagenDesdeUrl() {
        String imageUrlString = txtUrlPortada.getText();
        if (imageUrlString.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "URL Vacía", "Por favor, ingresa una URL de imagen válida.");
            return;
        }
        cargarImagen(imageUrlString);
    }

    private void cargarImagen(String imageUrlString) {
        try {
            URL url = new URL(imageUrlString);
            Image image = new Image(url.toExternalForm(), true);

            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0) {
                    if (image.isError()) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la imagen", "La URL proporcionada es inválida o el archivo no es una imagen.");
                        imageView.setImage(null);
                    } else {
                        imageView.setImage(image);
                    }
                }
            });

        } catch (MalformedURLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "URL Inválida", "La URL proporcionada no es un formato válido.");
            imageView.setImage(null);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Ocurrió un error inesperado al cargar la imagen. Verifique la URL.");
            e.printStackTrace();
            imageView.setImage(null);
        }
    }

    private void cargarAutores() {
        try {
            cmbAutor.getItems().clear();
            cmbAutor.getItems().addAll(autorDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar autores.", "Hubo un problema al obtener la lista de autores desde la base de datos: " + e.getMessage());
        }
    }

    private void cargarEditoriales() {
        try {
            cmbEditorial.getItems().clear();
            cmbEditorial.getItems().addAll(editorialDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar editoriales.", "Hubo un problema al obtener la lista de editoriales desde la base de datos: " + e.getMessage());
        }
    }

    private void cargarCategorias() {
        try {
            cmbCategoria.getItems().clear();
            cmbCategoria.getItems().addAll(categoriaDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar categorías.", "Hubo un problema al obtener la lista de categorías desde la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddAutor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Autor");
        dialog.setHeaderText("Ingresa el nombre completo del nuevo autor:");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombreCompleto -> {
            if (!nombreCompleto.trim().isEmpty()) {
                try {
                    Autor nuevoAutor = new Autor(0, nombreCompleto.trim());
                    autorDao.create(nuevoAutor);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Autor agregado correctamente.");
                    cargarAutores();
                    cmbAutor.getSelectionModel().select(nuevoAutor);
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar autor", "Hubo un problema al guardar el autor en la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre del autor no puede estar vacío.");
            }
        });
    }

    @FXML
    private void handleAddEditorial() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Editorial");
        dialog.setHeaderText("Ingresa el nombre de la nueva editorial:");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                try {
                    Editorial nuevaEditorial = new Editorial(0, nombre.trim());
                    editorialDao.create(nuevaEditorial);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Editorial agregada correctamente.");
                    cargarEditoriales();
                    cmbEditorial.getSelectionModel().select(nuevaEditorial);
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar editorial", "Hubo un problema al guardar la editorial en la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre de la editorial no puede estar vacío.");
            }
        });
    }

    @FXML
    private void handleAddCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Categoría");
        dialog.setHeaderText("Ingresa el nombre de la nueva categoría:");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            if (!nombre.trim().isEmpty()) {
                try {
                    Categoria nuevaCategoria = new Categoria(0, nombre.trim());
                    categoriaDao.create(nuevaCategoria);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría agregada correctamente.");
                    cargarCategorias();
                    cmbCategoria.getSelectionModel().select(nuevaCategoria);
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar categoría", "Hubo un problema al guardar la categoría en la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre de la categoría no puede estar vacío.");
            }
        });
    }

    @FXML
    private void guardarLibro() {
        if (txtIsbn.getText().trim().isEmpty() ||
                txtTitulo.getText().trim().isEmpty() ||
                cmbAutor.getSelectionModel().isEmpty() ||
                cmbEditorial.getSelectionModel().isEmpty() ||
                txtSinopsis.getText().trim().isEmpty() ||
                cmbCategoria.getSelectionModel().isEmpty() ||
                txtAnioPublicacion.getText().trim().isEmpty()) {

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos obligatorios del formulario.");
            return;
        }

        try {
            // --- NUEVA VALIDACIÓN: AÑO DE PUBLICACIÓN NO FUTURO ---
            int anioPublicacion = Integer.parseInt(txtAnioPublicacion.getText().trim());
            int anioActual = LocalDate.now().getYear();

            if (anioPublicacion > anioActual) {
                mostrarAlerta(Alert.AlertType.WARNING, "Año Inválido",
                        "El año de publicación no puede ser un año futuro. Por favor, revisa el dato.");
                return; // Detiene la ejecución si la validación falla
            }
            // --- FIN DE LA NUEVA VALIDACIÓN ---

            Libro libroExistente = libroDao.findByIsbn(txtIsbn.getText().trim());
            if (libroExistente != null && (libroEditado == null || libroExistente.getId() != libroEditado.getId())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error de Duplicidad",
                        "El libro con el ISBN '" + txtIsbn.getText().trim() + "' ya existe en la base de datos.");
                return;
            }

            Libro libro;
            if (libroEditado != null) {
                libro = libroEditado;
            } else {
                libro = new Libro();
            }

            libro.setIsbn(txtIsbn.getText().trim());
            libro.setTitulo(txtTitulo.getText().trim());
            libro.setAutor(cmbAutor.getValue());
            libro.setEditorial(cmbEditorial.getValue());
            libro.setResumen(txtSinopsis.getText().trim());
            libro.setCategoria(cmbCategoria.getValue());
            libro.setAnioPublicacion(anioPublicacion); // Usamos la variable ya parseada
            libro.setPortada(txtUrlPortada.getText().trim());

            // Asignar el estado por defecto "ACTIVO" al crear un nuevo libro
            if (libroEditado == null) {
                libro.setEstado("ACTIVO");
            }

            if (libroEditado != null) {
                libroDao.update(libro);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Libro actualizado correctamente.");
            } else {
                libroDao.create(libro);
                agregado = true;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Libro registrado correctamente.");
            }

            cerrarModal();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido", "El año de publicación debe ser un número válido (ej. 2023).");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de SQL", "Error al registrar el libro.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al registrar el libro.", e.getMessage());
        }
    }


    private void mostrarAlerta(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
