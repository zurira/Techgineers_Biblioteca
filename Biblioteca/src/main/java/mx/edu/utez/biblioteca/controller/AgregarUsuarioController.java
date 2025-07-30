package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AgregarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private Label lblFotoSeleccionada;

    private File archivoFoto;
    private UsuarioBiblioteca usuarioExistente;
    private boolean guardado = false;

    public boolean isGuardado() {
        return guardado;
    }

    public void setUsuarioExistente(UsuarioBiblioteca usuario) {
        this.usuarioExistente = usuario;
        txtNombre.setText(usuario.getNombre());
        dateNacimiento.setValue(usuario.getFechaNacimiento());
        txtEmail.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        lblFotoSeleccionada.setText("(foto actual)");
    }

    public void cargarDatosParaEdicion(UsuarioBiblioteca usuario) {
        setUsuarioExistente(usuario);
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar Fotografía");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );
        File selected = chooser.showOpenDialog(txtNombre.getScene().getWindow());

        if (selected != null) {
            archivoFoto = selected;
            lblFotoSeleccionada.setText(selected.getName());
        }
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || dateNacimiento.getValue() == null ||
                txtEmail.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            mostrarAlerta("Campos requeridos", "Completa todos los campos marcados con *");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps;
            String sql;

            if (usuarioExistente == null) {
                sql = "INSERT INTO agregar_usuarios (nombre, fecha_nacimiento, email, telefono, direccion, fotografia, activo) " +
                        "VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, 1)";
                ps = con.prepareStatement(sql);
            } else {
                sql = "UPDATE agregar_usuarios SET nombre = ?, fecha_nacimiento = TO_DATE(?, 'YYYY-MM-DD'), " +
                        "email = ?, telefono = ?, direccion = ?, fotografia = ? WHERE id_usuario = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(7, usuarioExistente.getId());
            }

            ps.setString(1, txtNombre.getText());
            ps.setString(2, dateNacimiento.getValue().toString());
            ps.setString(3, txtEmail.getText());
            ps.setString(4, txtTelefono.getText());
            ps.setString(5, txtDireccion.getText());

            if (archivoFoto != null) {
                try (FileInputStream fis = new FileInputStream(archivoFoto)) {
                    ps.setBinaryStream(6, fis, (int) archivoFoto.length());
                }
            } else {
                ps.setBinaryStream(6, null);
            }

            ps.executeUpdate();
            guardado = true;
            mostrarAlerta("Éxito", usuarioExistente == null ? "Usuario agregado correctamente." : "Usuario actualizado.");
            cancelar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        if (stage != null) stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}