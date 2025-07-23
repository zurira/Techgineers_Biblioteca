package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;

import java.util.List;
import java.io.IOException;

public class BienvenidaController {
    LibroDaoImpl libroDao = new LibroDaoImpl();

    @FXML private FlowPane contenedorLibros;
    @FXML
    private ScrollPane scrollLibros;

    @FXML
    public void initialize() {
        cargarLibros();
    }

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
        card.setAlignment(Pos.CENTER);
        ImageView portada = new ImageView();
        try {
            Image img = null;
            try {
                //System.out.println("Cargando desde: " + libro.getPortada());
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

        card.setOnMouseClicked(event -> {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/detallesModal.fxml"));
                Parent root = loader.load();

                DetallesModalController controller = loader.getController();
                controller.cargarDatos(libro); // El libro seleccionado

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Detalle del libro");
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return card;
    }



    @FXML
    private void irBusqueda(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/busqueda.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void irLogin() {
        // Falta redirigir a la vista de login
    }

}

