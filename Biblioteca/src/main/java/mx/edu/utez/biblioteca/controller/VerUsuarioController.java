package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import java.io.ByteArrayInputStream;

public class VerUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private ImageView imgFoto;
    @FXML private Button btnCerrar;

    // Método para cargar los detalles del usuario en la vista
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        // Deshabilitar la edición de los campos para que sea una vista de solo lectura
        txtNombre.setEditable(false);
        txtCorreo.setEditable(false);
        txtTelefono.setEditable(false);
        txtDireccion.setEditable(false);
        dpFechaNacimiento.setDisable(true);

        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtCorreo.setText(usuario.getCorreo());
            txtTelefono.setText(usuario.getTelefono());
            txtDireccion.setText(usuario.getDireccion());
            if (usuario.getFechaNacimiento() != null) {
                dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
            }

            // Lógica para cargar la fotografía desde la base de datos
            byte[] fotoBytes = usuario.getFotografia();
            if (fotoBytes != null && fotoBytes.length > 0) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(fotoBytes);
                    Image image = new Image(bis);
                    imgFoto.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error al cargar la foto del usuario: " + e.getMessage());
                    // Si hay un error, el ImageView se deja vacío.
                }
            }
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}