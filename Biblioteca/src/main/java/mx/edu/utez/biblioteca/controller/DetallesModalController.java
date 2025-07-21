package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Libro;

public class DetallesModalController {
    @FXML
    private Label titulo, autor, isbn, editorial, anio, resumen;
    @FXML private ImageView portada;
    @FXML
    Button btnCerrar;

    public void inicialize(Libro libro) {
        titulo.setText(libro.getTitulo());
        autor.setText("Autor: " + libro.getAutor());
        isbn.setText("ISBN: " + libro.getIsbn());
        editorial.setText("Editorial: " + libro.getEditorial());
        anio.setText("Año: " + libro.getAnioPublicacion());
        resumen.setText("Resumen: " + libro.getResumen());


        if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
            portada.setImage(new Image(libro.getPortada()));
        }
    }

    private void closeWindow(){
        Stage stage=(Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
