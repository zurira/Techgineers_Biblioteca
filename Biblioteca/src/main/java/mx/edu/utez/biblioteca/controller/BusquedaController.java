package mx.edu.utez.biblioteca.controller;

import javafx.animation.PauseTransition;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    @FXML private ScrollPane scrollLibros;
    @FXML private Label lblSinLibros;

    private LibroDaoImpl libroDao = new LibroDaoImpl();
    private CategoriaDaoImpl categoriaDao = new CategoriaDaoImpl();
    private PauseTransition debounceTimer;
    private final Map<String, Image> cacheImagenes = new HashMap<>();

    @FXML
    public void initialize() {
        // Inicializa el temporizador de debounce
        debounceTimer = new PauseTransition(Duration.millis(300));
        debounceTimer.setOnFinished(event -> filtrarLibros());

        // Carga las categorías y los libros inicialmente
        cargarCategorias();
        cargarLibros(null, null);

        // Cuando se escribe texto, se inicia el temporizador de debounce
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            debounceTimer.playFromStart();
        });

        // Cuando se selecciona una categoría, se filtra inmediatamente
        cmbCategoria.setOnAction(event -> {
            filtrarLibros();
        });
    }

    private void cargarCategorias() {
        List<String> categorias = categoriaDao.obtenerNombresCategorias();
        ObservableList<String> lista = FXCollections.observableArrayList();
        lista.add(""); // Opción vacía (sin filtro)
        lista.addAll(categorias);
        cmbCategoria.setItems(lista);
    }

    private void filtrarLibros() {
        final String texto = txtBuscar.getText().trim();
        String categoriaSeleccionada = cmbCategoria.getValue();
        final String categoria = (categoriaSeleccionada != null && categoriaSeleccionada.isEmpty()) ? null : categoriaSeleccionada;

        // Crear una tarea en segundo plano para la búsqueda de libros
        Task<List<Libro>> tareaFiltro = new Task<List<Libro>>() {
            @Override
            protected List<Libro> call() throws Exception {
                // Lógica de búsqueda en la base de datos (se ejecuta en hilo de fondo)
                return libroDao.obtenerLibrosPorFiltro(
                        texto == null ? "" : texto,
                        categoria
                );
            }
        };

        // Cuando la tarea se completa, actualizar la interfaz de usuario
        tareaFiltro.setOnSucceeded(event -> {
            List<Libro> libros = tareaFiltro.getValue();
            contenedorResultados.getChildren().clear();

            if (libros.isEmpty()) {
                lblSinLibros.setVisible(true);
            } else {
                lblSinLibros.setVisible(false);
                for (Libro libro : libros) {
                    VBox card = crearCardLibro(libro);
                    contenedorResultados.getChildren().add(card);
                }
            }
        });

        // Manejar posibles errores
        tareaFiltro.setOnFailed(event -> {
            lblSinLibros.setText("Ocurrió un error al cargar los libros.");
            lblSinLibros.setVisible(true);
        });

        // Iniciar la tarea en un nuevo hilo
        new Thread(tareaFiltro).start();
    }

    private void cargarLibros(String filtro, String categoria) {
        // Este método ahora crea un Task para manejar la carga inicial
        // y se puede llamar en initialize()
        final String filtroFinal = filtro == null ? "" : filtro;
        final String categoriaFinal = categoria;

        Task<List<Libro>> tareaCargaInicial = new Task<List<Libro>>() {
            @Override
            protected List<Libro> call() throws Exception {
                return libroDao.obtenerLibrosPorFiltro(
                        filtroFinal,
                        categoriaFinal
                );
            }
        };

        tareaCargaInicial.setOnSucceeded(event -> {
            List<Libro> libros = tareaCargaInicial.getValue();
            contenedorResultados.getChildren().clear();

            if (libros.isEmpty()) {
                lblSinLibros.setVisible(true);
            } else {
                lblSinLibros.setVisible(false);
                for (Libro libro : libros) {
                    VBox card = crearCardLibro(libro);
                    contenedorResultados.getChildren().add(card);
                }
            }
        });

        new Thread(tareaCargaInicial).start();
    }

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

        portada.setImage(new Image("https://via.placeholder.com/120x180.png?text=Cargando..."));

        final String url = libro.getPortada();

        if (cacheImagenes.containsKey(url)) {
            portada.setImage(cacheImagenes.get(url));
        } else {
            Task<Image> tareaCarga = new Task<>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(url, true);
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
            Region root = (Region) loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            root.prefWidthProperty().bind(stage.widthProperty());
            root.prefHeightProperty().bind(stage.heightProperty());
            Scene scene = new Scene(root);
            stage.setMaximized(true);
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
            Region root = (Region) loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            root.prefWidthProperty().bind(stage.widthProperty());
            root.prefHeightProperty().bind(stage.heightProperty());
            Scene scene = new Scene(root);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}