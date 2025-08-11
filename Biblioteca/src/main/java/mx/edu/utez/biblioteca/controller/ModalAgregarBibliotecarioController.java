package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.RolDaoImpl;
import mx.edu.utez.biblioteca.model.Usuario;
import mx.edu.utez.biblioteca.model.Rol;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
// Importaciones necesarias para la validación de la contraseña
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModalAgregarBibliotecarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasena;
    @FXML private TextArea txtDireccion;

    // Se eliminaron las variables FXML para los campos de estado y rol

    // Variable para almacenar el archivo de la imagen seleccionada
    private File imagenSeleccionada;

    private boolean agregado = false;

    public boolean seAgregoUsuario() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionarImagen;
    @FXML private ImageView imageView;


    @FXML
    public void initialize() {
        btnGuardar.setOnAction(event -> guardarBibliotecario());
        btnCancelar.setOnAction(event -> cerrarModal());
        // Asignar el evento al botón de seleccionar imagen
        btnSeleccionarImagen.setOnAction(event -> seleccionarImagen());
    }

    /**
     * Permite al usuario seleccionar un archivo de imagen del sistema,desde los archivos propios
     */
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Establecer filtros para tipos de archivo
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Imagen", Arrays.asList("*.png", "*.jpg", "*.jpeg"));
        fileChooser.getExtensionFilters().add(extFilter);

        // Obtener la ventana actual
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();

        // Mostrar el diálogo de selección de archivo
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Guardar el archivo seleccionado
                imagenSeleccionada = selectedFile;
                // Cargar y mostrar la imagen en el ImageView
                Image image = new Image(new FileInputStream(imagenSeleccionada));
                imageView.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen", "El archivo de imagen no se encontró.");
            }
        }
    }

    @FXML
    private void guardarBibliotecario() {
        String correo = txtCorreo.getText().trim();

        // Validación de campos incompletos
        // Se eliminó la validación para txtEstado.
        if (txtNombre.getText().trim().isEmpty() ||
                txtUsuario.getText().trim().isEmpty() ||
                correo.isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtContrasena.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                imagenSeleccionada == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos del formulario, incluyendo la selección de una foto.");
            return;
        }

        // --- VALIDACIÓN DE CORREO ---
        if (!correo.endsWith("@bibliotecario.com")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Correo Inválido", "El correo electrónico debe terminar en '@bibliotecario.com'.");
            return;
        }
        // --- FIN DE LA VALIDACIÓN ---

        // --- INICIO DE VALIDACIÓN DE CONTRASEÑA ---
        String contrasena = txtContrasena.getText();
        // La expresión regular valida:
        // 1. Al menos una minúscula (?=.*[a-z])
        // 2. Al menos una mayúscula (?=.*[A-Z])
        // 3. Al menos un carácter especial (?=.*[^a-zA-Z0-9])
        // 4. Longitud máxima de 5 caracteres, y mínima de 1 (.{1,5})
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{1,5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contrasena);

        if (!matcher.matches()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en Contraseña", "Contraseña Inválida", "La contraseña debe tener un máximo de 5 caracteres y contener al menos una mayúscula, una minúscula y un carácter especial.");
            return;
        }
        // --- FIN DE VALIDACIÓN DE CONTRASEÑA ---

        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        // El estado y el rol se asignan por defecto
        String estado = "S"; // El estado por defecto es "activo"
        int idRol = obtenerIdRolBibliotecario();

        if (idRol == -1) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error: No se pudo obtener el rol de Bibliotecario.");
            return;
        }

        byte[] fotoBytes = null;
        try {
            if (imagenSeleccionada != null)
                fotoBytes = Files.readAllBytes(imagenSeleccionada.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar la imagen", "Ocurrió un error al intentar leer el archivo de la imagen.");
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setUsername(usuario);
        nuevo.setCorreo(correo);
        nuevo.setTelefono(telefono);
        nuevo.setPassword(contrasena);
        nuevo.setEstado(estado); // Se asigna el estado por defecto
        nuevo.setDireccion(direccion);
        nuevo.setFoto(fotoBytes);

        Rol rol = new Rol();
        rol.setId(idRol);
        nuevo.setRol(rol); // Se asigna el rol por defecto

        try {
            new UsuarioDaoImpl().create(nuevo);
            agregado = true;
            mostrarAlerta(Alert.AlertType.INFORMATION, "¡Bibliotecario registrado exitosamente!");
            cerrarModal();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar bibliotecario.");
            e.printStackTrace();
        }
    }

    private int obtenerIdRolBibliotecario() {
        try {
            RolDaoImpl rolDao = new RolDaoImpl();
            Rol rol = rolDao.findByNombre("BIBLIOTECARIO");
            return rol != null ? rol.getId() : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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

    private void cerrarModal() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}
