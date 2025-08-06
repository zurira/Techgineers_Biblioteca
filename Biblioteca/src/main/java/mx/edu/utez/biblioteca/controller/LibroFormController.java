package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*; // Asegúrate de que estos imports sean correctos
import mx.edu.utez.biblioteca.model.*;   // Asegúrate de que estos imports sean correctos

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

public class LibroFormController {

    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Autor> cmbAutor; // Cambiado a ComboBox
    @FXML private ComboBox<Editorial> cmbEditorial; // Cambiado a ComboBox
    @FXML private TextArea txtSinopsis;
    @FXML private ComboBox<Categoria> cmbCategoria; // Cambiado a ComboBox
    @FXML private TextField txtAnioPublicacion;
    @FXML private TextField txtEstado;
    @FXML private TextField txtUrlPortada; // Campo para la URL de la portada

    @FXML private ImageView imageView;
    @FXML private Button btnCargarUrl; // Botón para cargar la imagen desde la URL
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Botones para agregar Autor, Editorial, Categoria
    @FXML private Button btnAgregarAutor;
    @FXML private Button btnAgregarEditorial;
    @FXML private Button btnAgregarCategoria;

    // DAOs para interactuar con la base de datos
    private LibroDaoImpl libroDao = new LibroDaoImpl();
    private AutorDaoImpl autorDao = new AutorDaoImpl();
    private EditorialDaoImpl editorialDao = new EditorialDaoImpl();
    private CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();

    private boolean agregado = false;
    private byte[] portadaBytes = null; // Para almacenar los bytes de la imagen cargada

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
     * También convierte la imagen a bytes para su posterior guardado.
     */
    @FXML // Asegúrate de que este método sea @FXML si se llama desde FXML
    private void cargarImagenDesdeUrl() {
        String imageUrlString = txtUrlPortada.getText();
        if (imageUrlString.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "URL Vacía", "Por favor, ingresa una URL de imagen válida.");
            return;
        }

        try {
            // Cargar la imagen desde la URL. El segundo parámetro 'true' permite la carga en segundo plano.
            Image image = new Image(imageUrlString, true);

            // Listener para monitorear el progreso de la carga de la imagen
            image.progressProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.doubleValue() == 1.0) { // Cuando la carga está completa (1.0 = 100%)
                    if (image.isError()) {
                        // Si hubo un error al cargar la imagen (ej. URL inválida, formato incorrecto)
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la imagen", "La URL proporcionada es inválida o el archivo no es una imagen.");
                        imageView.setImage(null); // Limpiar el ImageView
                        portadaBytes = null; // Limpiar los bytes de la portada
                    } else {
                        // Si la imagen se cargó correctamente
                        imageView.setImage(image);
                        // Convertir la imagen cargada a un array de bytes para poder guardarla en la BD
                        try {
                            URL url = new URL(imageUrlString);
                            InputStream is = url.openStream();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) != -1) {
                                bos.write(buffer, 0, len);
                            }
                            portadaBytes = bos.toByteArray();
                            is.close();
                        } catch (Exception e) {
                            // Error al procesar los bytes de la imagen después de la carga
                            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar la imagen", "Ocurrió un error al intentar procesar la imagen desde la URL.");
                            e.printStackTrace();
                            portadaBytes = null; // Asegurarse de que los bytes sean nulos si hay error
                        }
                    }
                }
            });
        } catch (Exception e) {
            // Captura cualquier otra excepción durante el intento inicial de crear la Image (ej. URL mal formada inicial)
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo cargar la imagen", "Ocurrió un error inesperado al cargar la imagen. Verifique la URL.");
            e.printStackTrace();
            portadaBytes = null; // Asegurarse de que los bytes sean nulos si hay error
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
    @FXML // Añadir @FXML a los métodos llamados desde onAction en FXML
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
    @FXML // Añadir @FXML a los métodos llamados desde onAction en FXML
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
    @FXML // Añadir @FXML a los métodos llamados desde onAction en FXML
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
                cmbAutor.getSelectionModel().isEmpty() || // Validar selección en ComboBox
                cmbEditorial.getSelectionModel().isEmpty() || // Validar selección en ComboBox
                txtSinopsis.getText().trim().isEmpty() ||
                cmbCategoria.getSelectionModel().isEmpty() || // Validar selección en ComboBox
                txtAnioPublicacion.getText().trim().isEmpty() ||
                txtEstado.getText().trim().isEmpty() ||
                portadaBytes == null) { // Validar que la imagen se haya cargado desde la URL

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos del formulario y asegúrate de cargar una portada desde la URL.");
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
            nuevoLibro.setAutor(cmbAutor.getValue()); // Obtener el objeto Autor seleccionado del ComboBox
            nuevoLibro.setEditorial(cmbEditorial.getValue()); // Obtener el objeto Editorial seleccionado del ComboBox
            nuevoLibro.setResumen(txtSinopsis.getText().trim());
            nuevoLibro.setCategoria(cmbCategoria.getValue()); // Obtener el objeto Categoria seleccionado del ComboBox
            nuevoLibro.setAnioPublicacion(Integer.parseInt(txtAnioPublicacion.getText().trim()));
            nuevoLibro.setEstado(estadoInput.toUpperCase()); // Usar el valor validado y en mayúsculas

            // Convertir los bytes de la imagen a Base64 para guardarla en la base de datos
            if (portadaBytes != null) {
                nuevoLibro.setPortada(Base64.getEncoder().encodeToString(portadaBytes));
            } else {
                nuevoLibro.setPortada(null); // Asegurarse de que sea null si no hay imagen
            }

            // Intentar guardar el libro en la base de datos
            libroDao.create(nuevoLibro);

            agregado = true; // Indicar que el libro se agregó correctamente
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Libro registrado exitosamente!", "El libro ha sido añadido a la biblioteca.");
            cerrarModal(); // Cerrar la ventana modal después de guardar

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido", "El año de publicación debe ser un número válido (ej. 2023).");
        } catch (Exception e) {
            e.printStackTrace(); // Imprimir la traza completa de la excepción para depuración
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
