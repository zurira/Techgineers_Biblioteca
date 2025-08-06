package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mx.edu.utez.biblioteca.model.Bibliotecario;

import java.net.URL;
import java.util.ResourceBundle;

public class EditarBibliotecarioController implements Initializable {

    @FXML private TextField txtNombre, txtCorreo, txtTelefono, txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private ComboBox<String> comboEstado;
    @FXML private TextArea txtDireccion;
    @FXML private ImageView imageView;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button btnGuardar, btnCancelar;

    private Bibliotecario bibliotecarioActual;
    private Bibliotecario bibliotecario;
    private byte[] nuevaFoto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboEstado.setItems(FXCollections.observableArrayList("S", "N"));
    }

    private void cargarDatos() {
        txtNombre.setText(bibliotecarioActual.getNombre());
        txtCorreo.setText(bibliotecarioActual.getCorreo());
        txtTelefono.setText(bibliotecarioActual.getTelefono());
        txtUsuario.setText(bibliotecarioActual.getUsername());
        txtDireccion.setText(bibliotecarioActual.getDireccion());
        comboEstado.setValue(bibliotecarioActual.getEstado());

        if (bibliotecarioActual.getFoto() != null) {
            Image img = new Image(new java.io.ByteArrayInputStream(bibliotecarioActual.getFoto()));
            imageView.setImage(img);
        }
    }
}
