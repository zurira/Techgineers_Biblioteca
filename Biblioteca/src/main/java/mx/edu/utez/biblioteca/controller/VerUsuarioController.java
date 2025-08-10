package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image; // Para mostrar la imagen
import javafx.scene.image.ImageView; // Para mostrar la imagen
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl; // Necesitas el DAO para cargar la foto si está en BLOB
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter; // Para formatear la fecha


public class VerUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private ImageView imgFoto; // Para mostrar la foto
    @FXML private Button btnCerrar;


    private UsuarioBiblioteca usuarioVer;

    // Método para cargar los detalles del usuario en la vista
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioVer = usuario;

        if (usuarioVer != null) {
            txtNombre.setText(usuarioVer.getNombre());

            // Formatear la fecha
            if (usuario.getFechaNacimiento() != null) {
                dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
            } else {
                dpFechaNacimiento.setValue(null);
            }

            txtCorreo.setText(usuarioVer.getCorreo());
            txtTelefono.setText(usuarioVer.getTelefono());
            txtDireccion.setText(usuarioVer.getDireccion());

            // Llama al método directamente con los bytes de la foto que ya están en el objeto
            cargarFotografia(usuarioVer.getFotografia());
        }
    }

    // Método para cargar la fotografía desde los bytes y manejar errores
    private void cargarFotografia(byte[] fotoBytes) {
        if (fotoBytes != null && fotoBytes.length > 0) {
            try {
                // Intenta crear la imagen desde los bytes
                Image image = new Image(new ByteArrayInputStream(fotoBytes));
                imgFoto.setImage(image);
            } catch (Exception e) {
                // Si hay un error, lo imprime y muestra una imagen por defecto
                e.printStackTrace();
                System.err.println("Error al cargar la fotografía del usuario: " + e.getMessage());
                imgFoto.setImage(new Image(getClass().getResourceAsStream("/mx/edu/utez/biblioteca/img/placeholder.png")));
            }
        } else {
            // Si no hay foto, usa una imagen por defecto
            imgFoto.setImage(new Image(getClass().getResourceAsStream("/mx/edu/utez/biblioteca/img/placeholder.png")));
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