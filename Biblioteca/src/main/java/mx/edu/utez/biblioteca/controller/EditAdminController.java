package mx.edu.utez.biblioteca.controller;




import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;

public class EditAdminController {

    @FXML private TextField tfNombre, tfCorreo, tfTelefono, tfUsuario, tfRol, tfEstado, tfDireccion;
    @FXML private PasswordField pfContrasena;
    @FXML private ImageView imageView;

    private File imagenSeleccionada;

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
        String sql = "UPDATE administradores SET nombre=?, correo=?, telefono=?, usuario=?, contrasena=?, rol=?, estado=?, direccion=?, foto=? WHERE usuario=?";
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "usuario", "contraseña");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tfNombre.getText());
            stmt.setString(2, tfCorreo.getText());
            stmt.setString(3, tfTelefono.getText());
            stmt.setString(4, tfUsuario.getText());
            stmt.setString(5, pfContrasena.getText());
            stmt.setString(6, tfRol.getText());
            stmt.setString(7, tfEstado.getText());
            stmt.setString(8, tfDireccion.getText());

            if (imagenSeleccionada != null) {
                InputStream fis = new FileInputStream(imagenSeleccionada);
                stmt.setBlob(9, fis);
            } else {
                stmt.setNull(9, Types.BLOB);
            }

            stmt.setString(10, tfUsuario.getText());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualizado", "Datos actualizados correctamente.");
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Error", "No se actualizó ningún registro.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error: " + e.getMessage());
        }
    }

    @FXML
    void onCancelar() {
        ((Button) tfNombre.getScene().lookup("#btnSeleccionarImagen")).getScene().getWindow().hide();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
