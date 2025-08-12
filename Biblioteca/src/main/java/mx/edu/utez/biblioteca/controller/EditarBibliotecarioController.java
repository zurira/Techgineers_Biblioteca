package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import mx.edu.utez.biblioteca.dao.impl.BibliotecarioDaoImpl;
import mx.edu.utez.biblioteca.model.Bibliotecario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
// Importaciones necesarias para la validación de la contraseña
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditarBibliotecarioController implements Initializable {

    @FXML
    private TextField txtNombre, txtCorreo, txtTelefono, txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private ComboBox<String> comboEstado;
    @FXML
    private TextArea txtDireccion;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnSeleccionarImagen;
    @FXML
    private Button btnGuardar, btnCancelar;

    private Bibliotecario bibliotecarioActual;
    private Bibliotecario bibliotecario;
    private byte[] nuevaFoto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // comboEstado.setItems(FXCollections.observableArrayList("S", "N"));
    }

    private void cargarDatos() {
        txtNombre.setText(bibliotecarioActual.getNombre());
        txtCorreo.setText(bibliotecarioActual.getCorreo());
        txtTelefono.setText(bibliotecarioActual.getTelefono());
        txtUsuario.setText(bibliotecarioActual.getUsername());
        txtPassword.setText(bibliotecarioActual.getPassword());
        txtDireccion.setText(bibliotecarioActual.getDireccion());
        //comboEstado.setValue(bibliotecarioActual.getEstado());

        if (bibliotecarioActual.getFoto() != null) {
            Image img = new Image(new java.io.ByteArrayInputStream(bibliotecarioActual.getFoto()));
            imageView.setImage(img);
        }
    }

    public void setBibliotecario(Bibliotecario bibliotecario) {
        this.bibliotecarioActual = bibliotecario;
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

    @FXML
    private void onGuardar() {
        String nuevaPass = txtPassword.isVisible() ? txtPassword.getText() : txtPasswordVisible.getText();

        // --- INICIO DE VALIDACIÓN DE CONTRASEÑA ---
        // Se valida solo si el usuario ha ingresado una nueva contraseña.
        if (!nuevaPass.isEmpty()) {
            // La expresión regular valida:
            // 1. Al menos una minúscula (?=.*[a-z])
            // 2. Al menos una mayúscula (?=.*[A-Z])
            // 3. Al menos un número (?=.*[0-9])
            // 4. Al menos un carácter especial (?=.*[^a-zA-Z0-9])
            // 5. Longitud máxima de 5 caracteres, y mínima de 1 (.{1,5})
            String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{12,100}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(nuevaPass);

            if (!matcher.matches()) {
                mostrarAlerta("Error en Contraseña", "La contraseña debe tener un minimo de 12 caracteres y contener al menos una mayúscula, una minúscula, un número y un carácter especial.");
                return; // Detiene el proceso si la contraseña es inválida
            }
        }
        // --- FIN DE VALIDACIÓN DE CONTRASEÑA ---

        try {
            bibliotecarioActual.setNombre(txtNombre.getText());
            bibliotecarioActual.setCorreo(txtCorreo.getText());
            bibliotecarioActual.setTelefono(txtTelefono.getText());
            bibliotecarioActual.setUsername(txtUsuario.getText());
            bibliotecarioActual.setDireccion(txtDireccion.getText());
            //bibliotecarioActual.setEstado(comboEstado.getValue());

            if (!nuevaPass.isEmpty()) {
                bibliotecarioActual.setPassword(nuevaPass); // Hashea si es necesario
            }

            if (nuevaFoto != null) {
                bibliotecarioActual.setFoto(nuevaFoto);
            }

            BibliotecarioDaoImpl dao = new BibliotecarioDaoImpl();
            boolean actualizado = dao.update(bibliotecarioActual);

            if (actualizado) {
                mostrarAlerta("Éxito", "Bibliotecario actualizado correctamente.");
                // Cerrar ventana o refrescar vista
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el bibliotecario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al actualizar.");
        }
    }

    @FXML
    private void onCancelar() {
        // Cierra la ventana actual
        btnCancelar.getScene().getWindow().hide();
    }
}
