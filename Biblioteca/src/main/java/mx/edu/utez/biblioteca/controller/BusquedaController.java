package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.CategoriaDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private final Map<String, Image> cacheImagenes = new HashMap<>();

    private VBox crearCardLibro(Libro libro) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card-libro");
        card.setPrefWidth(150);
        card.setAlignment(Pos.CENTER);

        ImageView portada = new ImageView();
        portada.setFitWidth(130);
        portada.setFitHeight(190);
        portada.setPreserveRatio(false);
        portada.setSmooth(true);
        portada.setCache(true);

        portada.setImage(new Image("https://via.placeholder.com/120x180.png?text=Cargando...")); // Imagen temporal

        String url = libro.getPortada();

        if (cacheImagenes.containsKey(url)) {
            portada.setImage(cacheImagenes.get(url));
        } else {
            Task<Image> tareaCarga = new Task<>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(url, true); // true = carga en background
                    } catch (Exception e) {
                        System.out.println("Error al cargar imagen: " + e.getMessage());
                        return new Image("https://via.placeholder.com/120x180.png?text=Sin+imagen");
                    }
                }
            };

            tareaCarga.setOnSucceeded(e -> {
                Image imagenCargada = tareaCarga.getValue();
                portada.setImage(imagenCargada);
                cacheImagenes.put(url, imagenCargada);
            });

            new Thread(tareaCarga).start();
        }

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
                controller.cargarDatos(libro);

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
