package mx.edu.utez.biblioteca.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.edu.utez.biblioteca.model.Rol;
import mx.edu.utez.biblioteca.model.Usuario;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class EditAdminController {

    @FXML private TextField tfNombre, tfCorreo, tfTelefono, tfUsuario, tfRol, tfDireccion;
    @FXML private PasswordField pfContrasena;
    @FXML private TextField tfContrasenaVisible;
    @FXML private Button btnTogglePassword;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ImageView imageView;

    private File imagenSeleccionada;
    private Usuario usuarioActual;
    private boolean mostrando = false;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        tfNombre.setText(usuario.getNombre());
        tfCorreo.setText(usuario.getCorreo());
        tfTelefono.setText(usuario.getTelefono());
        tfUsuario.setText(usuario.getUsername());
        pfContrasena.setText(usuario.getPassword());
        tfContrasenaVisible.setText(usuario.getPassword());
        tfRol.setText(usuario.getRol().getNombre());
        cbEstado.setValue(usuario.getEstado().equalsIgnoreCase("S") ? "Activo" : "Inactivo");
        tfDireccion.setText(usuario.getDireccion());

        if (usuario.getFoto() != null) {
            Image img = new Image(new ByteArrayInputStream(usuario.getFoto()));
            imageView.setImage(img);
        }
    }

    //Modifique esto para establecer la contraseña que sea de 12 caracteres
    @FXML
    public void initialize() {
        Tooltip tooltip = new Tooltip("La contraseña debe tener al menos:\n• 12 caracteres\n• Una mayúscula\n• Una minúscula\n• Un número\n• Un carácter especial");
        Tooltip.install(pfContrasena, tooltip);
        Tooltip.install(tfContrasenaVisible, tooltip);

        pfContrasena.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!mostrando) {
                tfContrasenaVisible.setText(newVal);
            }
            pfContrasena.setStyle(esContrasenaSegura(newVal) ? "-fx-border-color: green;" : "-fx-border-color: red;");
        });

        tfContrasenaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (mostrando) {
                pfContrasena.setText(newVal);
            }
            tfContrasenaVisible.setStyle(esContrasenaSegura(newVal) ? "-fx-border-color: green;" : "-fx-border-color: red;");
        });

        btnTogglePassword.setOnAction(e -> togglePasswordVisibility());
    }

    //Modifique esto para el ojo de la contraseña
    private void togglePasswordVisibility() {
        mostrando = !mostrando;

        tfContrasenaVisible.setVisible(mostrando);
        tfContrasenaVisible.setManaged(mostrando);

        pfContrasena.setVisible(!mostrando);
        pfContrasena.setManaged(!mostrando);

        FontIcon icon = new FontIcon(mostrando ? "fa-eye-slash" : "fa-eye");
        icon.setIconSize(16);
        btnTogglePassword.setGraphic(icon);
    }

    @FXML
    void onSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagenSeleccionada = file;
            Image img = new Image(file.toURI().toString());
            imageView.setImage(img);
        }
    }

    @FXML
    void onGuardar() {
        if (tfNombre.getText().isEmpty() || tfCorreo.getText().isEmpty() || tfUsuario.getText().isEmpty()
                || pfContrasena.getText().isEmpty() || tfRol.getText().isEmpty() || cbEstado.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor llena todos los campos obligatorios.");
            return;
        }

        try {
            Usuario usuarioEditado = new Usuario();
            usuarioEditado.setId(usuarioActual.getId());
            usuarioEditado.setNombre(tfNombre.getText());
            usuarioEditado.setCorreo(tfCorreo.getText());
            usuarioEditado.setTelefono(tfTelefono.getText());
            usuarioEditado.setUsername(tfUsuario.getText());
            usuarioEditado.setPassword(pfContrasena.getText());
            usuarioEditado.setDireccion(tfDireccion.getText());
            usuarioEditado.setEstado(cbEstado.getValue().equalsIgnoreCase("Activo") ? "S" : "N");

            Rol rol = new Rol();
            rol.setId(usuarioActual.getRol().getId());
            rol.setNombre(tfRol.getText());
            usuarioEditado.setRol(rol);

            if (imagenSeleccionada != null) {
                usuarioEditado.setFoto(Files.readAllBytes(imagenSeleccionada.toPath()));
            } else {
                usuarioEditado.setFoto(usuarioActual.getFoto());
            }

            UsuarioDaoImpl dao = new UsuarioDaoImpl();
            dao.update(usuarioEditado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Datos actualizados correctamente.");
            tfNombre.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error: " + e.getMessage());
        }
    }

    @FXML
    void onCancelar() {
        tfNombre.getScene().getWindow().hide();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
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
