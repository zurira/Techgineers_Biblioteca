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

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtContrasenaVisible;
    @FXML private Button btnTogglePassword;
    @FXML private TextField txtRol;
    @FXML private ComboBox<String> estadoComboBox; // CAMBIO: reemplaza txtEstado
    @FXML private TextArea txtDireccion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionarImagen;
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
            txtContrasena.setStyle(esContrasenaSegura(newVal) ? "-fx-border-color: green;" : "-fx-border-color: red;");
        });

        txtContrasenaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (mostrando) txtContrasena.setText(newVal);
            txtContrasenaVisible.setStyle(esContrasenaSegura(newVal) ? "-fx-border-color: green;" : "-fx-border-color: red;");
        });

        btnTogglePassword.setOnAction(e -> togglePasswordVisibility());
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
            Image imagen = new Image(imagenSeleccionada.toURI().toString());
            imageView.setImage(imagen);
        }
    }

    private void guardarAdministrador() {
        if (!camposValidos()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Campos incompletos");
            alert.setContentText("Por favor, llena todos los campos y selecciona una imagen antes de continuar.");
            alert.showAndWait();
            return;
        }

        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtContrasena.getText();

        // Este cambio modifique : obtener estado desde ComboBox
        String estadoSeleccionado = estadoComboBox.getValue();
        if (estadoSeleccionado == null || estadoSeleccionado.isEmpty()) {
            estadoComboBox.setStyle("-fx-border-color: red;");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Estado no seleccionado");
            alert.setContentText("Por favor, selecciona el estado del administrador.");
            alert.showAndWait();
            return;
        } else {
            estadoComboBox.setStyle(null);
        }
        String estado = estadoSeleccionado.equalsIgnoreCase("Activo") ? "S" : "N";

        String direccion = txtDireccion.getText().trim();
        int idRol = obtenerIdRolAdministrador();

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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("¡Administrador registrado exitosamente!");
            alert.showAndWait();
            cerrarModal();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al registrar");
            alert.setContentText("Ocurrió un problema al guardar el administrador.");
            alert.showAndWait();
        }
    }

    private boolean camposValidos() {
        if (txtNombre.getText().trim().isEmpty()) return false;
        if (txtCorreo.getText().trim().isEmpty()) return false;
        if (txtTelefono.getText().trim().isEmpty()) return false;
        if (txtUsuario.getText().trim().isEmpty()) return false;
        if (txtContrasena.getText().trim().isEmpty()) return false;
        if (txtRol.getText().trim().isEmpty()) return false;
        if (estadoComboBox.getValue() == null || estadoComboBox.getValue().trim().isEmpty()) return false; // CAMBIO
        if (txtDireccion.getText().trim().isEmpty()) return false;
        if (imagenSeleccionada == null) return false;

        return true;
    }

    private void cerrarModal() {
        btnCancelar.getScene().getWindow().hide();
    }

    private int obtenerIdRolAdministrador() {
        String sql = "SELECT ID FROM ROL WHERE NOMBRE = 'Administrador'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
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
