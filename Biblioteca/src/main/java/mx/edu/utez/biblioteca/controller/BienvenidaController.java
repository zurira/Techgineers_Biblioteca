package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;

import java.util.List;

public class BienvenidaController {
    LibroDaoImpl libroDao = new LibroDaoImpl();

    @FXML private FlowPane contenedorLibros;

    private void cargarLibros() {
        contenedorLibros.getChildren().clear();
        List<Libro> libros = libroDao.obtenerLibros();

        for (Libro libro : libros) {
            VBox card = crearCardLibro(libro);
            contenedorLibros.getChildren().add(card);
        }


    }

    private VBox crearCardLibro (Libro libro){
        VBox card = new VBox(10);
        card.getStyleClass().add("card-libro");
        card.setPrefWidth(150);
        ImageView portada = new ImageView();
        try {
            Image img = null;


        } catch (Exception e) {
            // En caso de error, se puede cargar una imagen por defecto
            portada.setImage(new Image("https://via.placeholder.com/120x180.png?text=Sin+imagen"));
        }
        return card;
    }


}

