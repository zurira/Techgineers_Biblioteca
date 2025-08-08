package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Bibliotecario;
import java.io.ByteArrayInputStream;

public class VerBibliotecarioController {

    @FXML
    private ImageView imgFoto;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextArea txtDireccion;

    @FXML
    private Button btnCerrar;

    public void setBibliotecario(Bibliotecario bibliotecario) {
        if (bibliotecario != null) {
            txtNombre.setText(bibliotecario.getNombre());
            txtCorreo.setText(bibliotecario.getCorreo());
            txtTelefono.setText(bibliotecario.getTelefono());
            txtUsuario.setText(bibliotecario.getUsername());

            // Convertir el estado de 'S'/'N' a 'Activo'/'Inactivo'
            String estadoTexto = "S".equals(bibliotecario.getEstado()) ? "Activo" : "Inactivo";
            txtEstado.setText(estadoTexto);

            // Mostrar la dirección
            txtDireccion.setText(bibliotecario.getDireccion());

            // Cargar la imagen si existe
            if (bibliotecario.getFoto() != null) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bibliotecario.getFoto());
                    Image image = new Image(bis);
                    imgFoto.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error al cargar la foto del bibliotecario: " + e.getMessage());
                    // Puedes dejar la imagen por defecto o mostrar un mensaje de error
                }
            }
        }
    }

    // Acción para cerrar la ventana
    @FXML
    private void onClose(ActionEvent event) {
        // Obtener el Stage actual a partir del botón y cerrarlo
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
