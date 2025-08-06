package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.edu.utez.biblioteca.model.Bibliotecario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecario = bibliotecario;

        txtNombre.setText(bibliotecario.getNombre());
        txtCorreo.setText(bibliotecario.getCorreo());
        txtTelefono.setText(bibliotecario.getTelefono());
        txtUsuario.setText(bibliotecario.getUsername());
        txtPassword.setText(bibliotecario.getPassword());

        cargarDatos();
    }

    @FXML
    private void togglePasswordVisibility() {
        boolean visible = txtPasswordVisible.isVisible();
        if (visible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
        }
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                nuevaFoto = fis.readAllBytes();
                imageView.setImage(new Image(new java.io.ByteArrayInputStream(nuevaFoto)));
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


}
