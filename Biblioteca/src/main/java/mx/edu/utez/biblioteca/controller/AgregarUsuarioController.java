// AgregarUsuarioController.java (con soporte para insertar/editar usuario y seleccionar foto)
package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import database.ConexionDB;
import model.UsuarioBiblioteca;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class AgregarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private Label lblFoto;

    private File fotoSeleccionada;
    private UsuarioBiblioteca usuarioExistente = null;
    private boolean guardado = false;

    public void setUsuarioExistente(UsuarioBiblioteca usuario) {
        this.usuarioExistente = usuario;
        txtNombre.setText(usuario.getNombre());
        dateNacimiento.setValue(LocalDate.parse(usuario.getFechaNacimiento()));
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        lblFoto.setText(usuario.getFotoNombre());
    }

    public boolean isGuardado() {
        return guardado;
    }

    public UsuarioBiblioteca getUsuario() {
        return usuarioExistente;
    }

    @FXML
    private void cancelar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || dateNacimiento.getValue() == null ||
                txtEmail.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Por favor, completa todos los campos marcados con *.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql;
            PreparedStatement stmt;

            String nombreFoto = (fotoSeleccionada != null) ? fotoSeleccionada.getName() :
                    (usuarioExistente != null ? usuarioExistente.getFotoNombre() : null);

            if (usuarioExistente == null) {
                sql = "INSERT INTO usuarios (nombre, fecha_nacimiento, email, telefono, direccion, foto) VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "UPDATE usuarios SET nombre = ?, fecha_nacimiento = TO_DATE(?, 'YYYY-MM-DD'), email = ?, telefono = ?, direccion = ?, foto = ? WHERE id_usuario = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(7, usuarioExistente.getIdUsuario());
            }

            ps.setString(1, txtNombre.getText());
            ps.setString(2, dateNacimiento.getValue().toString());
            ps.setString(3, txtEmail.getText());
            ps.setString(4, txtTelefono.getText());
            ps.setString(5, txtDireccion.getText());
            ps.setString(6, nombreFoto);

            ps.executeUpdate();

            if (fotoSeleccionada != null) {
                Files.copy(fotoSeleccionada.toPath(),
                        new File("src/main/resources/fotos/" + fotoSeleccionada.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            guardado = true;
            mostrarAlerta("Éxito", usuarioExistente == null ? "Usuario agregado correctamente." : "Usuario editado correctamente.");
            cancelar();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el usuario:\n" + e.getMessage());
        }
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar fotografía");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        fotoSeleccionada = fileChooser.showOpenDialog(txtNombre.getScene().getWindow());
        if (fotoSeleccionada != null) {
            lblFoto.setText(fotoSeleccionada.getName());
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}