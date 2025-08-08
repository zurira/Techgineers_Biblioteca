package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Libro;

public class VerLibroController {

    @FXML
    private ImageView imgPortada;

    @FXML
    private TextField txtIsbn;

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextField txtAutor;

    @FXML
    private TextField txtEditorial;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtAnioPublicacion;

    @FXML
    private TextArea txtSinopsis;

    @FXML
    private Button btnCerrar;

    /**
     * Este método recibe un objeto Libro y lo utiliza para popular todos los campos
     * de la interfaz de usuario.
     * @param libro El objeto Libro del que se mostrará la información.
     */
    public void setLibro(Libro libro) {
        if (libro != null) {
            // Llenar los campos de texto
            txtIsbn.setText(libro.getIsbn());
            txtTitulo.setText(libro.getTitulo());

            // Obtener los nombres del autor, editorial y categoría con manejo de nulos
            txtAutor.setText(libro.getAutor() != null ? libro.getAutor().getNombreCompleto() : "N/A");
            txtEditorial.setText(libro.getEditorial() != null ? libro.getEditorial().getNombre() : "N/A");
            txtCategoria.setText(libro.getCategoria() != null ? libro.getCategoria().getNombre() : "N/A");

            txtAnioPublicacion.setText(String.valueOf(libro.getAnioPublicacion()));

            // Llenar la sinopsis con manejo de nulos
            txtSinopsis.setText(libro.getResumen() != null ? libro.getResumen() : "Sin sinopsis.");

            // Cargar la imagen de la portada desde la URL con manejo de nulos
            String urlPortada = libro.getPortada();
            if (urlPortada != null && !urlPortada.isEmpty()) {
                try {
                    Image image = new Image(urlPortada);
                    imgPortada.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error al cargar la imagen de la portada: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Cierra la ventana del modal al presionar el botón "Cerrar".
     * @param event El evento de la acción del botón.
     */
    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
