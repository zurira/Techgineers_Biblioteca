package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.impl.AdministradorDao;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModalAgregarAdminController {

    @FXML private TextField txtNombre, txtCorreo, txtTelefono, txtUsuario, txtRol;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtContrasenaVisible;
    @FXML private Button btnTogglePassword;
    @FXML private TextArea txtDireccion;
    @FXML private Button btnGuardar, btnCancelar, btnSeleccionarImagen;
    @FXML private ImageView imageView;

    private File imagenSeleccionada;
    private boolean agregado = false;
    private boolean mostrando = false;

    public boolean seAgregoUsuario() {
        return agregado;
    }

    @FXML
    public void initialize() {
        btnSeleccionarImagen.setOnAction(event -> seleccionarImagen());
        btnGuardar.setOnAction(event -> guardarAdministrador());
        btnCancelar.setOnAction(event -> cerrarModal());

        Tooltip tooltip = new Tooltip("La contraseña debe tener al menos:\n• 12 caracteres\n• Una mayúscula\n• Una minúscula\n• Un número\n• Un carácter especial");
        Tooltip.install(txtContrasena, tooltip);
        Tooltip.install(txtContrasenaVisible, tooltip);

        txtContrasena.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!mostrando) txtContrasenaVisible.setText(newVal);
            actualizarEstiloCampo(txtContrasena, newVal);
        });

        txtContrasenaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (mostrando) txtContrasena.setText(newVal);
            actualizarEstiloCampo(txtContrasenaVisible, newVal);
        });

        btnTogglePassword.setOnAction(e -> togglePasswordVisibility());
    }

    private void actualizarEstiloCampo(TextField campo, String valor) {
        campo.setStyle(esContrasenaSegura(valor) ? "-fx-border-color: green;" : "-fx-border-color: red;");
    }

    private void togglePasswordVisibility() {
        mostrando = !mostrando;
        txtContrasenaVisible.setVisible(mostrando);
        txtContrasenaVisible.setManaged(mostrando);
        txtContrasena.setVisible(!mostrando);
        txtContrasena.setManaged(!mostrando);

        FontIcon icon = new FontIcon(mostrando ? "fa-eye-slash" : "fa-eye");
        icon.setIconSize(16);
        btnTogglePassword.setGraphic(icon);
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg")
        );

        imagenSeleccionada = fileChooser.showOpenDialog(null);
        if (imagenSeleccionada != null) {
            imageView.setImage(new Image(imagenSeleccionada.toURI().toString()));
        }
    }

    private void guardarAdministrador() {
        if (!camposValidos()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llena todos los campos y selecciona una imagen antes de continuar.");
            return;
        }

        // Validación solo acepta  números y espacios no tiene mínimo de digitos

        String telefonoSinEspacios = txtTelefono.getText().trim().replaceAll("\\s+", "");

        if (!telefonoSinEspacios.matches("\\d+")) {
            txtTelefono.setStyle("-fx-border-color: red;");
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", "Solo se permiten números y espacios. No se aceptan letras ni símbolos.");
            return;
        } else {
            txtTelefono.setStyle("-fx-border-color: green;");
        }




        String correo = txtCorreo.getText().trim();
        //String usuario = txtUsuario.getText().trim();
        String usuarioDuplicado = txtUsuario.getText().trim();

        //  Validación de dominio
        if (!correo.endsWith("@administrador.com")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Solo se permiten correos que terminen con '@administrador.com'.");
            return;
        }

        //Agrego esto nuevo para evitar datos duplicados
        if (AdministradorDao.existeAdministrador(correo, usuarioDuplicado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Administrador duplicado", "Ya existe un administrador con ese correo o usuario.");
            return;
        }




        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtContrasena.getText();
        String direccion = txtDireccion.getText().trim();
        int idRol = obtenerIdRolAdministrador();

        String estado = "S"; // Estado fijo como "Activo"

        InputStream fotoStream = null;
        try {
            if (imagenSeleccionada != null)
                fotoStream = new FileInputStream(imagenSeleccionada);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean exito = AdministradorDao.insertarAdministrador(
                nombre, usuario, correo, telefono,
                contrasena, fotoStream, idRol, estado, direccion
        );

        if (exito) {
            agregado = true;
            mostrarAlerta(Alert.AlertType.INFORMATION, null, "¡Administrador registrado exitosamente!");
            cerrarModal();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar", "Ocurrió un problema al guardar el administrador.");
        }
    }

    private boolean camposValidos() {
        return !txtNombre.getText().trim().isEmpty()
                && !txtCorreo.getText().trim().isEmpty()
                && !txtTelefono.getText().trim().isEmpty()
                && !txtUsuario.getText().trim().isEmpty()
                && !txtContrasena.getText().trim().isEmpty()
                && !txtRol.getText().trim().isEmpty()
                && !txtDireccion.getText().trim().isEmpty()
                && imagenSeleccionada != null;
    }

    private void cerrarModal() {
        btnCancelar.getScene().getWindow().hide();
    }

    private int obtenerIdRolAdministrador() {
        String sql = "SELECT ID FROM ROL WHERE NOMBRE = 'Administrador'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private boolean esContrasenaSegura(String contrasena) {
        if (contrasena == null || contrasena.length() < 12) return false;

        boolean tieneMayuscula = contrasena.matches(".*[A-Z].*");
        boolean tieneMinuscula = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*\\d.*");
        boolean tieneEspecial = contrasena.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|].*");

        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial;
    }






}
