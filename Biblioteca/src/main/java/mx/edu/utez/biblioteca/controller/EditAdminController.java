package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mx.edu.utez.biblioteca.model.Rol;
import mx.edu.utez.biblioteca.model.Usuario;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class EditAdminController {

    @FXML private TextField tfNombre, tfCorreo, tfTelefono, tfUsuario, tfRol, tfDireccion;
    @FXML private PasswordField pfContrasena;
    @FXML private ComboBox<String> cbEstado;
    @FXML private ImageView imageView;

    private File imagenSeleccionada;
    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        tfNombre.setText(usuario.getNombre());
        tfCorreo.setText(usuario.getCorreo());
        tfTelefono.setText(usuario.getTelefono());
        tfUsuario.setText(usuario.getUsername());
        pfContrasena.setText(usuario.getPassword());
        tfRol.setText(usuario.getRol().getNombre());
        cbEstado.setValue(usuario.getEstado().equalsIgnoreCase("S") ? "Activo" : "Inactivo");
        tfDireccion.setText(usuario.getDireccion());

        if (usuario.getFoto() != null) {
            Image img = new Image(new ByteArrayInputStream(usuario.getFoto()));
            imageView.setImage(img);
        }
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

    //ESTO AGREGUE/MODIFIQUE
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
}
