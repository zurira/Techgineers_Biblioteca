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
            // Formatear la fecha para una mejor presentación
            if (usuario.getFechaNacimiento() != null) {
                dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
            } else {
                dpFechaNacimiento.setValue(null); // O un valor por defecto si no hay fecha
            }
            txtCorreo.setText(usuarioVer.getCorreo());
            txtTelefono.setText(usuarioVer.getTelefono());
            txtDireccion.setText(usuarioVer.getDireccion());

            // Cargar y mostrar la fotografía (requiere modificar UsuarioBibliotecaDaoImpl.findById)
            cargarFotografia(usuarioVer.getId()); // Cargar la foto por ID
        }
    }

    // Nuevo método para cargar la fotografía desde la base de datos
    private void cargarFotografia(int idUsuario) {
        try {
            UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();
            byte[] fotoBytes = dao.getFotografiaById(idUsuario); // Necesitarás este método en tu DAO

            if (fotoBytes != null && fotoBytes.length > 0) {
                Image image = new Image(new ByteArrayInputStream(fotoBytes));
                imgFoto.setImage(image);
            } else {
                imgFoto.setImage(null); // No hay foto, limpiar ImageView
                // Puedes cargar una imagen de placeholder si lo deseas:
                // imgFoto.setImage(new Image(getClass().getResourceAsStream("/mx/edu/utez/biblioteca/img/placeholder.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar error al cargar la foto, por ejemplo, mostrar una imagen de error
            imgFoto.setImage(null);
            System.err.println("Error al cargar la fotografía del usuario: " + e.getMessage());
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