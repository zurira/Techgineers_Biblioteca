package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.Ejemplar;

public class DetallesModalController {

    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo, lblAutor, lblEditorial, lblAnio;
    @FXML private TextArea txtResumen;

    public void cargarDatos(Libro libro) {
        if (libro == null) return;

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor().getNombreCompleto());
        lblEditorial.setText(libro.getEditorial().getNombre());
        lblAnio.setText("Publicado en: " + libro.getAnioPublicacion());
        txtResumen.setText(libro.getResumen());
        imgPortada.setImage(new Image(libro.getPortada()));
    }
}
