package mx.edu.utez.biblioteca.controller;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.impl.AdministradorDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModalAgregarAdminController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtRol;
    @FXML private TextField txtEstado;
    //@FXML private TextField txtDireccion;

    // Indica si se agregó un administrador exitosamente
    private boolean agregado = false;

    // Getter para que el controlador padre sepa si debe refrescar
    public boolean seAgregoUsuario() {
        return agregado;
    }

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
        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String contrasena = txtContrasena.getText();
        String estado = txtEstado.getText().trim().equalsIgnoreCase("activo") ? "S" : "N";
        String direccion = txtDireccion.getText().trim();
        int idRol = obtenerIdRolAdministrador(); // método que buscará el ID del rol "Administrador"

        InputStream fotoStream = null;
        try {
            if (imagenSeleccionada != null)
                fotoStream = new FileInputStream(imagenSeleccionada);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean exito = AdministradorDao.insertarAdministrador(
                nombre, usuario, correo, telefono,
                contrasena, fotoStream, idRol, estado, direccion
        );

        if (exito) {
            agregado = true; //  Marcamos éxito de forma interna

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("¡Administrador registrado exitosamente!");
            alert.showAndWait();
            cerrarModal();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al registrar");
            alert.setContentText("Ocurrió un problema al guardar el administrador.");
            alert.showAndWait();
        }
    }


    private void cerrarModal() {
        btnCancelar.getScene().getWindow().hide();
    }


    private int obtenerIdRolAdministrador() {
        String sql = "SELECT ID FROM ROL WHERE NOMBRE = 'Administrador'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; // Valor por defecto en caso de no encontrar el rol
    }



}