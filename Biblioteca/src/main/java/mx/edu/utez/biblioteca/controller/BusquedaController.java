package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.CategoriaDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;

import java.io.IOException;
import java.util.List;

public class BusquedaController {
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private FlowPane contenedorResultados;

    LibroDaoImpl libroDao = new LibroDaoImpl();
    CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();

    @FXML
    public void initialize() {
        // Carga las categorías y los libros
        cargarCategorias();
        cargarLibros(null, null);

        // cuando se escribe texto, se filtra
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarLibros();
        });

        //cuando se selecciona una categoría, se filtra
        cmbCategoria.setOnAction(event -> {
            filtrarLibros();
        });
    }


    private void cargarCategorias() {
        List<String> categorias = categoriaDao.obtenerNombresCategorias();
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add(""); // opción vacía (sin filtro)
        lista.addAll(categorias);
        cmbCategoria.setItems(lista);
    }


    private void filtrarLibros() {
        String texto = txtBuscar.getText().trim();
        String categoria = cmbCategoria.getValue();

        if (categoria != null && categoria.isEmpty()) {
            categoria = null;
        }

        cargarLibros(texto, categoria);
    }

    private void cargarLibros(String filtro, String categoria) {
        contenedorResultados.getChildren().clear();

        List<Libro> libros = libroDao.obtenerLibrosPorFiltro(
                filtro == null ? "" : filtro,
                categoria
        );

        for (Libro libro : libros) {
            VBox card = crearCardLibro(libro);
            contenedorResultados.getChildren().add(card);
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
    private void irInicio(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/bienvenida.fxml"));
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
    private void irLogin(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
