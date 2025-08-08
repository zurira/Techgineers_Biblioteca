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

public class ModalAgregarBibliotecarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasena;
    @FXML private TextArea txtDireccion;
    @FXML private TextField txtEstado;

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
        // Validación de campos
        if (txtNombre.getText().trim().isEmpty() ||
                txtUsuario.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtContrasena.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                txtEstado.getText().trim().isEmpty() ||
                imagenSeleccionada == null) {

            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Campos incompletos", "Por favor, llena todos los campos del formulario, incluyendo la selección de una foto.");
            return; // En caso de que no se llenen todos los campos detiene la ejecución si hay campos vacíos
        }

        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtContrasena.getText();
        String estado = txtEstado.getText().trim().equalsIgnoreCase("activo") ? "S" : "N";
        String direccion = txtDireccion.getText().trim();

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
            // Mostrar una alerta si hay un error al leer el archivo
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al procesar la imagen", "Ocurrió un error al intentar leer el archivo de la imagen.");
            return; // Detener el guardado si hay un problema con la imagen
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setUsername(usuario);
        nuevo.setCorreo(correo);
        nuevo.setTelefono(telefono);
        nuevo.setPassword(contrasena);
        nuevo.setEstado(estado);
        nuevo.setDireccion(direccion);
        nuevo.setFoto(fotoBytes);

        Rol rol = new Rol();
        rol.setId(idRol);
        nuevo.setRol(rol);

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
