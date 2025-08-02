package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image; // Para mostrar la imagen
import javafx.scene.image.ImageView; // Para mostrar la imagen
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl; // Necesitas el DAO para cargar la foto si está en BLOB
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter; // Para formatear la fecha


public class VerUsuarioController {

    @FXML private Label lblNombre;
    @FXML private Label lblFechaNacimiento;
    @FXML private Label lblCorreo;
    @FXML private Label lblTelefono;
    @FXML private Label lblDireccion;
    @FXML private Label lblEstado;
    @FXML private ImageView imgFoto; // Para mostrar la foto

    private UsuarioBiblioteca usuarioVer;

    // Método para cargar los detalles del usuario en la vista
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioVer = usuario;

        if (usuarioVer != null) {
            lblNombre.setText(usuarioVer.getNombre());
            // Formatear la fecha para una mejor presentación
            if (usuarioVer.getFechaNacimiento() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                lblFechaNacimiento.setText(usuarioVer.getFechaNacimiento().format(formatter));
            } else {
                lblFechaNacimiento.setText("N/A");
            }
            lblCorreo.setText(usuarioVer.getCorreo());
            lblTelefono.setText(usuarioVer.getTelefono());
            lblDireccion.setText(usuarioVer.getDireccion());
            lblEstado.setText("S".equalsIgnoreCase(usuarioVer.getEstado()) ? "Activo" : "Inactivo");

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
        Stage stage = (Stage) lblNombre.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}