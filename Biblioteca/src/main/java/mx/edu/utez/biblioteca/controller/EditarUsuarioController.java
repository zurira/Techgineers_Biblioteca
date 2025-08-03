package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.File;
import java.time.LocalDate; // Importa LocalDate


public class EditarUsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private DatePicker dpFechaNacimiento; // <-- AÑADIDO: Para la fecha de nacimiento
    @FXML private Button btnGuardar, btnSeleccionarImagen;
    @FXML private Button btnCancelar;

    private UsuarioBiblioteca usuarioEditando;
    private boolean guardado = false;

    // Método que se llama desde DashboardController para pasar el usuario a editar
    public void cargarUsuario(UsuarioBiblioteca usuario) {
        this.usuarioEditando = usuario;
        txtNombre.setText(usuario.getNombre());
        txtCorreo.setText(usuario.getCorreo());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());

        // Manejo de la fecha de nacimiento
        if (usuario.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(usuario.getFechaNacimiento());
        } else {
            dpFechaNacimiento.setValue(null); // O un valor por defecto si no hay fecha
        }
    }

    @FXML
    private void onGuardar() {
        // Validaciones
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "El nombre y correo son requeridos.");
            return;
        }
        if (!txtCorreo.getText().contains("@")) {
            mostrarAlerta("Correo inválido", "Introduce un correo válido.");
            return;
        }

        // Asigna los valores de los campos al objeto UsuarioBiblioteca
        usuarioEditando.setNombre(txtNombre.getText().trim());
        usuarioEditando.setCorreo(txtCorreo.getText().trim());
        usuarioEditando.setTelefono(txtTelefono.getText().trim());
        usuarioEditando.setDireccion(txtDireccion.getText().trim());
        usuarioEditando.setFechaNacimiento(dpFechaNacimiento.getValue()); // <-- Asigna la fecha de nacimiento

        try {
            UsuarioBibliotecaDaoImpl dao = new UsuarioBibliotecaDaoImpl();
            // Asegúrate de que tu método update() maneje correctamente la fecha de nacimiento y todos los campos
            dao.update(usuarioEditando);
            guardado = true; // Indica que se guardó correctamente
            mostrarAlerta("Usuario actualizado", "Los datos se guardaron correctamente.");
            cerrarVentana(); // Cierra el modal
        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error en consola para depuración
            mostrarAlerta("Error", "No se pudo actualizar el usuario: " + e.getMessage());
        }
    }

    // Método para que el controlador padre (DashboardController) sepa si se guardó el usuario
    public boolean isGuardado() {
        return guardado;
    }

    // Método para cerrar la ventana modal
    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    // Método para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void seleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de usuario");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif") // Añadido .gif
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(btnSeleccionarImagen.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnSeleccionarImagen.setText(archivoSeleccionado.getName());
            // Aquí deberías guardar la ruta o copiar el archivo a tu proyecto si quieres persistirlo
            // usuarioEditando.setRutaFoto(archivoSeleccionado.getAbsolutePath()); // Si tienes un campo para esto
        } else {
            btnSeleccionarImagen.setText("Sin archivo seleccionado");
        }
    }

    @FXML
    public void cancelar(ActionEvent event) {
        cerrarVentana(); // Simplemente cierra el modal sin guardar
    }

    // El método cambiarEstado ya no es necesario como onAction separado si actualizarTextoYEstiloToggle
    // está en el listener y en la inicialización.
    // Si lo mantienes como onAction, asegúrate de que solo llame a actualizarTextoYEstiloToggle().
    /*
    @FXML
    public void cambiarEstado(ActionEvent event) {
        actualizarTextoYEstiloToggle(); // Usa el método unificado
    }
    */
}