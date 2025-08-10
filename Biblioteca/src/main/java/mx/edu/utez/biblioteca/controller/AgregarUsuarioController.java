package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.io.FileInputStream;

public class AgregarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;
    @FXML private ImageView imgFotoPerfil;

    private byte[] fotografiaBytes; //guardar bytes de la foto
    private File archivoFoto;
    private UsuarioBiblioteca usuarioExistente;
    private boolean guardado = false;
    private final UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();

    public boolean isGuardado() {
        return guardado;
    }

    public void cargarDatosParaEdicion(UsuarioBiblioteca usuario) {
        this.usuarioExistente = usuario;
        txtNombre.setText(usuario.getNombre());
        dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());
        btnSeleccionarImagen.setText("(foto actual)");
    }

    @FXML
    private void seleccionarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar Fotografía");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.jpeg", "*.png")
        );
        File selected = chooser.showOpenDialog(btnCancelar.getScene().getWindow());
        if (selected != null) {
            archivoFoto = selected;
            btnSeleccionarImagen.setText(selected.getName());

            try {
                // Leer el archivo en un array de bytes
                FileInputStream fis = new FileInputStream(selected);
                this.fotografiaBytes = new byte[(int) selected.length()];
                fis.read(this.fotografiaBytes);
                fis.close();

                Image image = new Image(selected.toURI().toString());
                imgFotoPerfil.setImage(image);
            } catch (Exception e) {
                System.err.println("Error al cargar la imagen: " + e.getMessage());
                this.fotografiaBytes = null; // En caso de error, resetea la variable
            }
        }
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || dpFechaNacimiento.getValue() == null ||
                txtCorreo.getText().isEmpty() || txtTelefono.getText().isEmpty() ||
            txtDireccion.getText().isEmpty())
        {
            mostrarAlerta("Campos requeridos", "Completa todos los campos marcados con *");
            return;
        }
        if (this.archivoFoto == null) {
            System.out.println("Error: La foto es obligatoria.");
            return; // Esto detiene la ejecución del método.
        }

        try {
            UsuarioBiblioteca usuario = new UsuarioBiblioteca();
            usuario.setNombre(txtNombre.getText());
            usuario.setFechaNacimiento(dpFechaNacimiento.getValue());
            usuario.setCorreo(txtCorreo.getText());
            usuario.setTelefono(txtTelefono.getText());
            usuario.setDireccion(txtDireccion.getText());
            usuario.setEstado("Activo");

            if (usuarioExistente == null) {
                // Nuevo usuario
                dao.create(usuario, archivoFoto);
                mostrarAlerta("Éxito", "Usuario agregado correctamente.");
            } else {
                // Actualización
                usuario.setId(usuarioExistente.getId());
                dao.update(usuario, archivoFoto);
                mostrarAlerta("Éxito", "Usuario actualizado correctamente.");
            }

            guardado = true;
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el usuario: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar(ActionEvent e) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
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