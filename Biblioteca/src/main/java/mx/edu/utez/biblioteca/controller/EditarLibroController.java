package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*;
import mx.edu.utez.biblioteca.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditarLibroController implements Initializable {

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
    @FXML private Spinner<Integer> spinnerCantidadEjemplares;
    @FXML private TextField txtUbicacion;

    // Instancias de los DAOs
    private LibroDaoImpl libroDao = new LibroDaoImpl();
    private AutorDaoImpl autorDao = new AutorDaoImpl();
    private EditorialDaoImpl editorialDao = new EditorialDaoImpl();
    private CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();
    private final EjemplarDaoImpl ejemplarDao = new EjemplarDaoImpl();

    private Libro libroActual;
    private Ejemplar ejemplarActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Carga los datos de los comboboxes al iniciar el controlador
        cargarAutores();
        cargarEditoriales();
        cargarCategorias();

        // Configura los listeners para los botones
        btnGuardar.setOnAction(event -> onGuardar());
        btnCancelar.setOnAction(event -> onCancelar());
        btnCargarUrl.setOnAction(event -> cargarImagenDesdeUrl());

        // Configura un Tooltip para el botón de cargar imagen
        Tooltip cargarImagenTooltip = new Tooltip("Da click para cargar la imagen del libro desde la URL");
        Tooltip.install(btnCargarUrl, cargarImagenTooltip);
    }

    /**
     * Carga el objeto Libro en el formulario para su edicion.
     * @param libro El libro a editar.
     */
    public void setLibro(Libro libro) {
        this.libroActual = libro;
        cargarDatos();
    }

    /**
     * Carga los datos del libroActual en los campos del formulario.
     */
    private void cargarDatos() {
        if (libroActual != null) {
            txtIsbn.setText(libroActual.getIsbn());
            txtTitulo.setText(libroActual.getTitulo());
            txtSinopsis.setText(libroActual.getResumen());
            txtAnioPublicacion.setText(String.valueOf(libroActual.getAnioPublicacion()));
            txtUrlPortada.setText(libroActual.getPortada());

            // Selecciona el autor, editorial y categoría correctos
            cmbAutor.getSelectionModel().select(libroActual.getAutor());
            cmbEditorial.getSelectionModel().select(libroActual.getEditorial());
            cmbCategoria.getSelectionModel().select(libroActual.getCategoria());

            // Carga la imagen desde la URL si existe
            if (libroActual.getPortada() != null && !libroActual.getPortada().isEmpty()) {
                cargarImagen(libroActual.getPortada());
            }
        }
    }

    /**
     * Carga la imagen en el ImageView desde la URL del campo de texto.
     */
    @FXML
    private void cargarImagenDesdeUrl() {
        String imageUrlString = txtUrlPortada.getText();
        if (imageUrlString.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "URL Vacía", "Por favor, ingresa una URL de imagen válida.");
            return;
        }
        cargarImagen(imageUrlString);
    }

    /**
     * Carga una imagen en el ImageView desde una URL.
     * @param imageUrlString La URL de la imagen.
     */
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
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Ocurrió un error inesperado al cargar la imagen. Verifique la URL.");
            e.printStackTrace();
            imageView.setImage(null);
        }
    }

    /**
     * Carga los autores desde la base de datos en el ComboBox.
     */
    private void cargarAutores() {
        try {
            cmbAutor.getItems().clear();
            cmbAutor.getItems().addAll(autorDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar autores.", "Hubo un problema al obtener la lista de autores desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Carga las editoriales desde la base de datos en el ComboBox.
     */
    private void cargarEditoriales() {
        try {
            cmbEditorial.getItems().clear();
            cmbEditorial.getItems().addAll(editorialDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar editoriales.", "Hubo un problema al obtener la lista de editoriales desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Carga las categorías desde la base de datos en el ComboBox.
     */
    private void cargarCategorias() {
        try {
            cmbCategoria.getItems().clear();
            cmbCategoria.getItems().addAll(categoriaDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar categorías.", "Hubo un problema al obtener la lista de categorías desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de guardar el libro modificado.
     */
    @FXML
    private void onGuardar() {
        if (txtIsbn.getText().trim().isEmpty() ||
                txtTitulo.getText().trim().isEmpty() ||
                cmbAutor.getSelectionModel().isEmpty() ||
                cmbEditorial.getSelectionModel().isEmpty() ||
                txtSinopsis.getText().trim().isEmpty() ||
                cmbCategoria.getSelectionModel().isEmpty() ||
                txtAnioPublicacion.getText().trim().isEmpty() ||
                txtUrlPortada.getText().trim().isEmpty() ||
                txtUbicacion.getText().trim().isEmpty()){

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos obligatorios del formulario.");
            return;
        }

        try {
            int anioPublicacion = Integer.parseInt(txtAnioPublicacion.getText().trim());
            int anioActual = LocalDate.now().getYear();

            if (anioPublicacion > anioActual) {
                mostrarAlerta(Alert.AlertType.WARNING, "Año Inválido",
                        "El año de publicación no puede ser un año futuro. Por favor, revisa el dato.");
                return;
            }

            // Actualiza el objeto libroActual con los nuevos datos del formulario
            libroActual.setIsbn(txtIsbn.getText().trim());
            libroActual.setTitulo(txtTitulo.getText().trim());
            libroActual.setAutor(cmbAutor.getValue());
            libroActual.setEditorial(cmbEditorial.getValue());
            libroActual.setResumen(txtSinopsis.getText().trim());
            libroActual.setCategoria(cmbCategoria.getValue());
            libroActual.setAnioPublicacion(anioPublicacion);
            libroActual.setPortada(txtUrlPortada.getText().trim());
            // El estado ya no se actualiza desde el formulario.

            libroDao.update(libroActual);

            // Se verifica si se deben insertar nuevos ejemplares
            int cantidad = spinnerCantidadEjemplares.getValue();
            String ubicacion = txtUbicacion.getText().trim();

            if (cantidad > 0) {
                if (ubicacion.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "La ubicación no puede estar vacía si deseas agregar ejemplares.");
                    return;
                }

                boolean exito = ejemplarDao.insertarVariosEjemplares(libroActual.getId(), cantidad, ubicacion);
                if (exito) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Libro actualizado y se agregaron " + cantidad + " ejemplares.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "Libro actualizado, pero hubo un problema al agregar los ejemplares.");
                }
            } else {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Libro actualizado correctamente.");
            }

            cerrarModal();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido", "El año de publicación debe ser un número válido (ej. 2023).");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de SQL", "Error al actualizar el libro.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar el libro.", e.getMessage());
        }
    }


    @FXML
    private void onCancelar() {
        // Este método cierra la ventana o modal sin guardar los cambios
        System.out.println("Cancelando la edición...");
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }


    /**
     * Muestra una alerta con un titulo y mensaje.
     */
    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta con titulo, encabezado y mensaje.
     */
    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana del modal.
     */
    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

