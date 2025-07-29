package mx.edu.utez.biblioteca.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class ModalAgregarAdminController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtRol;
    @FXML private TextField txtEstado;
    //@FXML private TextField txtDireccion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionarImagen;
    @FXML private ImageView imageView;
    @FXML
    private TextArea txtDireccion;

    private File imagenSeleccionada;

    @FXML
    public void initialize() {
        btnSeleccionarImagen.setOnAction(event -> seleccionarImagen());
        btnGuardar.setOnAction(event -> guardarAdministrador());
        btnCancelar.setOnAction(event -> cerrarModal());
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png", "*.jpeg")
        );

        imagenSeleccionada = fileChooser.showOpenDialog(null);
        if (imagenSeleccionada != null) {
            Image imagen = new Image(imagenSeleccionada.toURI().toString());
            imageView.setImage(imagen);
        }
    }


    private void guardarAdministrador() {
        System.out.println("Guardando administrador:");
        System.out.println("Nombre: " + txtNombre.getText());
        // Aquí puedes agregar lógica para guardar en base de datos o backend.
    }


    