package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
            try {
                System.out.println("Cargando desde: " + libro.getPortada());
                img = new Image(libro.getPortada(), false);  //Se carga la url remota de la base de datos
                portada.setImage(img);
            } catch (Exception e) {
                //En caso de error con la carga de la imagen, el programa sigue y se carga una imagen por defecto
                System.out.println("Error al cargar imagen: " + e.getMessage());
                portada.setImage(new Image("https://via.placeholder.com/120x180.png?text=Sin+imagen"));

            }

        } catch (Exception e) {
            // En caso de error, se puede cargar una imagen por defecto
            portada.setImage(new Image("https://via.placeholder.com/120x180.png?text=Sin+imagen"));
        }

        portada.setFitWidth(130);
        portada.setFitHeight(190);
        portada.setPreserveRatio(false);
        portada.setSmooth(true);
        portada.setCache(true);

        Label titulo = new Label(libro.getTitulo());
        titulo.getStyleClass().add("titulo-libro");
        titulo.setWrapText(true);
        titulo.setMaxWidth(130);

        card.getChildren().addAll(portada, titulo);

        card.setOnMouseClicked(event -> mostrarDetalles(libro)); // evento el cual al pasar el mouse se mostrará un modal con más información del libro

        return card;
    }
    private void mostrarDetalles(Libro libro) {
        // Falta crear el modal
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("ISBN: " + libro.getIsbn());
        System.out.println("Resumen: " + libro.getResumen());
        System.out.println("Año: " + libro.getAnioPublicacion());
    }

}

