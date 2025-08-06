package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*; // Asegúrate de que estos imports sean correctos
import mx.edu.utez.biblioteca.model.*;   // Asegúrate de que estos imports sean correctos

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class LibroFormController {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Autor> cmbAutor;
    @FXML private ComboBox<Editorial> cmbEditorial;
    @FXML private TextArea txtSinopsis;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtAnioPublicacion;
    @FXML private TextField txtEstado;
    @FXML private TextField txtUrlPortada; // Campo para la URL de la portada

    @FXML private ImageView imageView;
    @FXML private Button btnCargarUrl; // Botón para cargar la imagen desde la URL
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Botones para agregar Autor, Editorial, Categoria
    @FXML private Button btnAgregarAutor;
    @FXML private Button btnAgregarEditorial; // <-- ¡Corregido!
    @FXML private Button btnAgregarCategoria;

    // DAOs para interactuar con la base de datos
    private LibroDaoImpl libroDao = new LibroDaoImpl();
    private AutorDaoImpl autorDao = new AutorDaoImpl();
    private EditorialDaoImpl editorialDao = new EditorialDaoImpl();
    private CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();

    private boolean agregado = false;

    // Valores correctos de la base de datos para la restricción de control de ESTADO
    private static final String VALOR_ACTIVO = "ACTIVO";
    private static final String VALOR_INACTIVO = "INACTIVO";

    public boolean seAgregoLibro() {
        return agregado;
    }

    @FXML
    public void initialize() {
        // Asignar manejadores de eventos a los botones
        btnGuardar.setOnAction(event -> guardarLibro());
        btnCancelar.setOnAction(event -> cerrarModal());
        btnCargarUrl.setOnAction(event -> cargarImagenDesdeUrl());

        // Asignar acciones a los nuevos botones de agregar
        btnAgregarAutor.setOnAction(event -> handleAddAutor());
        btnAgregarEditorial.setOnAction(event -> handleAddEditorial());
        btnAgregarCategoria.setOnAction(event -> handleAddCategoria());

        // Cargar datos iniciales en los ComboBox al iniciar el controlador
        cargarAutores();
        cargarEditoriales();
        cargarCategorias();
    }

    /**
     * Carga la imagen desde la URL ingresada en txtUrlPortada y la muestra en el ImageView.
     * Solo carga y visualiza la imagen, no la convierte a bytes.
     */
    @FXML
    private void cargarImagenDesdeUrl() {
        String imageUrlString = txtUrlPortada.getText();
        if (imageUrlString.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "URL Vacía", "Por favor, ingresa una URL de imagen válida.");
            return;
        }

        try {
            // Cargar la imagen desde la URL.
            // El segundo parámetro 'true' permite la carga en segundo plano.
            // Usamos el constructor que acepta una URL para evitar errores con cadenas mal formadas
            URL url = new URL(imageUrlString);
            Image image = new Image(url.toExternalForm(), true);

            // Listener para monitorear el progreso de la carga de la imagen
            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0) { // Cuando la carga está completa
                    if (image.isError()) {
                        // Si hubo un error al cargar la imagen
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la imagen", "La URL proporcionada es inválida o el archivo no es una imagen.");
                        imageView.setImage(null); // Limpiar el ImageView
                    } else {
                        // Si la imagen se cargó correctamente
                        imageView.setImage(image);
                    }
                }
            });

        } catch (MalformedURLException e) {
            // Captura si la URL no está bien formada
            mostrarAlerta(Alert.AlertType.ERROR, "URL Inválida", "La URL proporcionada no es un formato válido.");
            imageView.setImage(null);
            e.printStackTrace();
        } catch (Exception e) {
            // Captura cualquier otra excepción durante el intento de cargar la imagen
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Ocurrió un error inesperado al cargar la imagen. Verifique la URL.");
            e.printStackTrace();
            imageView.setImage(null);
        }
    }


    /**
     * Carga la lista de autores desde la base de datos y los agrega al ComboBox cmbAutor.
     */
    private void cargarAutores() {
        try {
            cmbAutor.getItems().clear(); // Limpiar elementos existentes antes de cargar nuevos
            cmbAutor.getItems().addAll(autorDao.findAll()); // Añadir todos los autores encontrados
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar autores.", "Hubo un problema al obtener la lista de autores desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de editoriales desde la base de datos y los agrega al ComboBox cmbEditorial.
     */
    private void cargarEditoriales() {
        try {
            cmbEditorial.getItems().clear(); // Limpiar elementos existentes
            cmbEditorial.getItems().addAll(editorialDao.findAll()); // Añadir todas las editoriales
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar editoriales.", "Hubo un problema al obtener la lista de editoriales desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de categorías desde la base de datos y los agrega al ComboBox cmbCategoria.
     */
    private void cargarCategorias() {
        try {
            cmbCategoria.getItems().clear(); // Limpiar elementos existentes
            cmbCategoria.getItems().addAll(categoriaDao.findAll()); // Añadir todas las categorías
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "Error al cargar categorías.", "Hubo un problema al obtener la lista de categorías desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Abre un cuadro de diálogo para que el usuario ingrese el nombre de un nuevo autor.
     * Si el nombre es válido, intenta crear el autor en la base de datos y recarga el ComboBox.
     */
    @FXML
    private void handleAddAutor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Autor");
        dialog.setHeaderText("Ingresa el nombre completo del nuevo autor:");
        dialog.setContentText("Nombre:");

        Optional<String> result = dialog.showAndWait(); // Espera la entrada del usuario
        result.ifPresent(nombreCompleto -> { // Si el usuario ingresó algo y presionó OK
            if (!nombreCompleto.trim().isEmpty()) { // Validar que el nombre no esté vacío
                try {
                    Autor nuevoAutor = new Autor(0, nombreCompleto.trim());
                    // Intentar crear el autor en la base de datos
                    if (autorDao.create(nuevoAutor)) { // Se espera que create devuelva boolean
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Autor agregado correctamente.");
                        cargarAutores(); // Recargar el ComboBox para mostrar el nuevo autor
                        // Seleccionar el nuevo autor en el ComboBox si fue agregado exitosamente
                        cmbAutor.getSelectionModel().select(nuevoAutor);
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar autor", "No se pudo agregar el autor a la base de datos.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar autor", "Hubo un problema al guardar el autor en la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre del autor no puede estar vacío.");
            }
        });
    }

    /**
     * Abre un cuadro de diálogo para que el usuario ingrese el nombre de una nueva editorial.
     * Si el nombre es válido, intenta crear la editorial en la base de datos y recarga el ComboBox.
     */
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
                    if (editorialDao.create(nuevaEditorial)) { // Se espera que create devuelva boolean
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Editorial agregada correctamente.");
                        cargarEditoriales(); // Recargar el ComboBox
                        cmbEditorial.getSelectionModel().select(nuevaEditorial);
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar editorial", "No se pudo agregar la editorial a la base de datos.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar editorial", "Hubo un problema al guardar la editorial en la base de datos: " + e.getMessage());
                }
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "El nombre de la editorial no puede estar vacío.");
            }
        });
    }

    /**
     * Abre un cuadro de diálogo para que el usuario ingrese el nombre de una nueva categoría.
     * Si el nombre es válido, intenta crear la categoría en la base de datos y recarga el ComboBox.
     */
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
                    if (categoriaDao.create(nuevaCategoria)) { // Se espera que create devuelva boolean
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría agregada correctamente.");
                        cargarCategorias(); // Recargar el ComboBox
                        cmbCategoria.getSelectionModel().select(nuevaCategoria);
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR, "Error al agregar categoría", "No se pudo agregar la categoría a la base de datos.");
                    }
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
        // Validar que todos los campos obligatorios estén llenos
        if (txtIsbn.getText().trim().isEmpty() ||
                txtTitulo.getText().trim().isEmpty() ||
                cmbAutor.getSelectionModel().isEmpty() ||
                cmbEditorial.getSelectionModel().isEmpty() ||
                txtSinopsis.getText().trim().isEmpty() ||
                cmbCategoria.getSelectionModel().isEmpty() ||
                txtAnioPublicacion.getText().trim().isEmpty() ||
                txtEstado.getText().trim().isEmpty() ||
                txtUrlPortada.getText().trim().isEmpty()) { // Validar que el campo de URL no esté vacío

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos del formulario y asegúrate de ingresar una URL de portada.");
            return;
        }

        // Validación específica para el campo Estado
        String estadoInput = txtEstado.getText().trim();
        if (!VALOR_ACTIVO.equalsIgnoreCase(estadoInput) && !VALOR_INACTIVO.equalsIgnoreCase(estadoInput)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Estado Inválido", "El estado del libro debe ser '" + VALOR_ACTIVO + "' o '" + VALOR_INACTIVO + "'.");
            return;
        }

        try {
            Libro nuevoLibro = new Libro();
            nuevoLibro.setIsbn(txtIsbn.getText().trim());
            nuevoLibro.setTitulo(txtTitulo.getText().trim());
            nuevoLibro.setAutor(cmbAutor.getValue());
            nuevoLibro.setEditorial(cmbEditorial.getValue());
            nuevoLibro.setResumen(txtSinopsis.getText().trim());
            nuevoLibro.setCategoria(cmbCategoria.getValue());
            nuevoLibro.setAnioPublicacion(Integer.parseInt(txtAnioPublicacion.getText().trim()));
            nuevoLibro.setEstado(estadoInput.toUpperCase());
            nuevoLibro.setPortada(txtUrlPortada.getText().trim()); // Asignar la URL directamente

            // Intentar guardar el libro en la base de datos
            libroDao.create(nuevoLibro);

            agregado = true;
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Libro registrado exitosamente!", "El libro ha sido añadido a la biblioteca.");
            cerrarModal();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido", "El año de publicación debe ser un número válido (ej. 2023).");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al registrar el libro.", e.getMessage());
        }
    }

    // --- Métodos sobrecargados para mostrar alertas ---

    /**
     * Muestra una ventana de alerta con un tipo y un mensaje.
     * El encabezado será nulo.
     * @param type Tipo de alerta (INFORMATION, WARNING, ERROR, etc.)
     * @param message Contenido del cuerpo de la alerta.
     */
    private void mostrarAlerta(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra una ventana de alerta con un tipo, título y contenido.
     * El encabezado será nulo.
     * @param type Tipo de alerta.
     * @param title Título de la ventana de alerta.
     * @param content Contenido del cuerpo de la alerta.
     */
    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // El encabezado es nulo en esta versión
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Muestra una ventana de alerta con un tipo, título, encabezado y contenido.
     * @param type Tipo de alerta.
     * @param title Título de la ventana de alerta.
     * @param header Encabezado de la alerta.
     * @param content Contenido del cuerpo de la alerta.
     */
    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}
