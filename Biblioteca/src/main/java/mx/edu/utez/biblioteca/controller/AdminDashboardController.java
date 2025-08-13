package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.EjemplarDaoImpl;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.model.Libro;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

//Controller de administrador
public class AdminDashboardController {

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnAddLibro;

    @FXML
    private TableView<Libro> tableViewLibros;

    @FXML
    private TableColumn<Libro, Integer> colNo;

    @FXML
    private TableColumn<Libro, String> colISBN;

    @FXML
    private TableColumn<Libro, String> colTituloLibro;

    @FXML
    private TableColumn<Libro, String> colAutor;

    @FXML
    private TableColumn<Libro, String> colEditorial;

    @FXML
    private TableColumn<Libro, String> colResumen;

    @FXML
    private TableColumn<Libro, String> colCategoria;

    @FXML
    private TableColumn<Libro, Integer> colAnioPublicacion;

    // Se agrego la columna estado
    @FXML
    private TableColumn<Libro, String> colEstado;

    @FXML
    private TableColumn<Libro, Integer> colStock;

    @FXML
    private TableColumn<Libro, Void> colAcciones;

    @FXML
    private Label lblSinResultados;

    private LibroDaoImpl libroDao;
    private ObservableList<Libro> listaLibros;

    // Valores correctos de la base de datos para la restricción de control
    private static final String VALOR_ACTIVO = "ACTIVO";
    private static final String VALOR_INACTIVO = "INACTIVO";


    @FXML
    public void initialize() {
        libroDao = new LibroDaoImpl();
        configurarColumnasTabla();
        lblSinResultados.setVisible(false);
        tableViewLibros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarLibros();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarLibros(newValue);
        });

        // Agrega un listener al botón de cerrar sesión si ya esta el fx:id
        if (btnLogOut != null) {
            btnLogOut.setOnAction(event -> onLogOut());
        }
    }

    //configuraciones de las columnas de la taba libros
    private void configurarColumnasTabla() {
        colNo.setCellValueFactory(null); // No necesita una propiedad del objeto Libro
        colNo.setCellFactory(column -> {
            return new TableCell<Libro, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        // Muestra el índice de la fila + 1
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            };
        });

        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTituloLibro.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        colAutor.setCellValueFactory(cellData -> {
            Autor autor = cellData.getValue().getAutor();
            return new SimpleStringProperty(autor != null ? autor.getNombreCompleto() : "N/A");
        });

        colEditorial.setCellValueFactory(cellData -> {
            Editorial editorial = cellData.getValue().getEditorial();
            return new SimpleStringProperty(editorial != null ? editorial.getNombre() : "N/A");
        });

        colCategoria.setCellValueFactory(cellData -> {
            Categoria categoria = cellData.getValue().getCategoria();
            return new SimpleStringProperty(categoria != null ? categoria.getNombre() : "N/A");
        });

        colAnioPublicacion.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));

        // Se configura la columna de estado
        colEstado.setCellValueFactory(cellData -> {
            if (VALOR_ACTIVO.equals(cellData.getValue().getEstado())) {
                return new SimpleStringProperty("Activo");
            } else {
                return new SimpleStringProperty("Inactivo");
            }
        });
        colEstado.setCellFactory(column -> {
            return new TableCell<Libro, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Label label = new Label(item);
                        label.setAlignment(Pos.CENTER);
                        label.getStyleClass().add("status-label");

                        if ("Activo".equals(item)) {
                            label.getStyleClass().add("status-active");
                        } else {
                            label.getStyleClass().add("status-inactive");
                        }
                        this.setAlignment(Pos.CENTER);
                        setGraphic(label);
                        setText(null);
                    }
                }
            };
        });

        colStock.setCellValueFactory(new PropertyValueFactory<>("stockDisponible"));

        // Columna de Acciones (Editar, ver y cambiar estado)
        colAcciones.setCellFactory(param -> new TableCell<Libro, Void>() {
            private final Button editButton = new Button();
            private final Button changeStatusButton = new Button();
            private final Button viewLibroButton = new Button();

            {
                // Botón para editar
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar libro"));

                // Botón para ver información
                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewLibroButton.setGraphic(viewIcon);
                viewLibroButton.getStyleClass().add("action-button");
                viewLibroButton.setTooltip(new Tooltip("Ver información del libro"));

                // Botón para cambiar el estado (el "switch")
                changeStatusButton.getStyleClass().add("action-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro libro = getTableView().getItems().get(getIndex());

                    FontIcon statusIcon;

                    // Lógica para el "switch" de estado
                    if (VALOR_ACTIVO.equals(libro.getEstado())) {
                        statusIcon = new FontIcon("fa-toggle-on");
                        changeStatusButton.setTooltip(new Tooltip("Desactivar"));
                    } else {
                        statusIcon = new FontIcon("fa-toggle-off");
                        changeStatusButton.setTooltip(new Tooltip("Activar"));
                    }

                    statusIcon.getStyleClass().add("action-icon");
                    changeStatusButton.setGraphic(statusIcon);

                    // Asigna las acciones a los botones
                    editButton.setOnAction(event -> onEditLibro(libro));
                    // Aquí se enlaza el botón "ver" con el nuevo método
                    viewLibroButton.setOnAction(event -> onViewLibro(libro));
                    changeStatusButton.setOnAction(event -> onChangeStatusLibro(libro));

                    // Se crea el HBox con el orden de los botones: Editar, Ver, Cambiar estado
                    HBox buttons = new HBox(5, editButton, viewLibroButton, changeStatusButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarLibros() {
        Task<List<Libro>> cargaLibrosTask = new Task<>() {
            @Override
            protected List<Libro> call() throws Exception {
                // Este código se ejecuta en un hilo de fondo, no congela la interfaz
                EjemplarDaoImpl ejemplarDao = new EjemplarDaoImpl();
                List<Libro> libros = libroDao.findAll();

                // Alternativa 1: Consulta optimizada para cargar el stock de todos los libros a la vez
                Map<Integer, Integer> stockPorLibro = ejemplarDao.obtenerStockPorLibro();

                for (Libro libro : libros) {
                    int idLibro = libro.getId();
                    int stock = stockPorLibro.getOrDefault(idLibro, 0);
                    libro.setStockDisponible(stock);
                }
                return libros;
            }
        };

        cargaLibrosTask.setOnSucceeded(event -> {
            // Esta parte se ejecuta en el hilo principal de la UI
            try {
                List<Libro> librosCargados = cargaLibrosTask.getValue();
                listaLibros = FXCollections.observableArrayList(librosCargados);
                tableViewLibros.setItems(listaLibros);
                lblSinResultados.setVisible(listaLibros.isEmpty());
            } catch (Exception e) {
                // Manejo de errores si algo falla después de la carga exitosa
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Visualización", "No se pudieron mostrar los datos.", "Hubo un error al procesar los resultados de la base de datos.");
            }
        });

        cargaLibrosTask.setOnFailed(event -> {
            // Manejo de errores si la tarea del hilo de fondo falla
            Throwable e = cargaLibrosTask.getException();
            e.printStackTrace();
            System.err.println("Error al cargar libros en el hilo de fondo: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los libros",
                    "Hubo un error al intentar obtener los datos de la base de datos. Por favor, revisa la conexión.");
        });

        // Inicia la tarea en un nuevo hilo
        new Thread(cargaLibrosTask).start();
    }

    private void filtrarLibros(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tableViewLibros.setItems(listaLibros);
            lblSinResultados.setVisible(false);
            return;
        }

        String filtroLower = filtro.toLowerCase();

        ObservableList<Libro> librosFiltrados = FXCollections.observableArrayList();

        for (Libro libro : listaLibros) {
            String titulo = libro.getTitulo() != null ? libro.getTitulo().toLowerCase() : "";
            String isbn = libro.getIsbn() != null ? libro.getIsbn().toLowerCase() : "";
            String autorNombre = libro.getAutor() != null ? libro.getAutor().getNombreCompleto().toLowerCase() : "";
            String editorialNombre = libro.getEditorial() != null ? libro.getEditorial().getNombre().toLowerCase() : "";
            String categoriaNombre = libro.getCategoria() != null ? libro.getCategoria().getNombre().toLowerCase() : "";
            String resumen = libro.getResumen() != null ? libro.getResumen().toLowerCase() : "";

            if (titulo.contains(filtroLower) || isbn.contains(filtroLower) ||
                    autorNombre.contains(filtroLower) || editorialNombre.contains(filtroLower) ||
                    categoriaNombre.contains(filtroLower) || resumen.contains(filtroLower)) {
                librosFiltrados.add(libro);
            }
        }

        tableViewLibros.setItems(librosFiltrados);
        lblSinResultados.setVisible(librosFiltrados.isEmpty());
    }

    @FXML
    // Método para añadir libro
    private void onAddLibro() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/agregarLibro.fxml"));
            Parent root = fxmlLoader.load();

            LibroFormController modalController = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Agregar Libro");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // Si se agregó un libro, recargar la tabla
            if (modalController.isAgregado()) {
                cargarLibros();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario.", "Hubo un error al intentar cargar la vista de agregar libro.");
        }
    }
    // implementando el método para editar libro

    private void onEditLibro(Libro libro) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editarLibro.fxml"));
            Parent root = fxmlLoader.load();

            EditarLibroController controller = fxmlLoader.getController();
            controller.setLibro(libro);

            // Creamos y mostramos la ventana modal
            Stage stage = new Stage();
            stage.setTitle("Editar Bibliotecario");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Usamos showAndWait() para bloquear la ventana principal hasta que el modal se cierre
            stage.showAndWait();

            cargarLibros();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario.", "Hubo un error al intentar cargar la vista de agregar libro");
        }
    }

    /**
     * @param libro el libro cuyos detalles se verán.
     * Método para ver la información detallada del libro.
     */
    private void onViewLibro(Libro libro) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/VerLibro.fxml"));
            Parent root = fxmlLoader.load();

            VerLibroController controller = fxmlLoader.getController();
            controller.setLibro(libro);

            Stage stage = new Stage();
            stage.setTitle("Ver Libro");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario.", "Hubo un error al intentar cargar la vista de ver libro.");
        }
    }

    // Método para cambiar el estado del libro
    private void onChangeStatusLibro(Libro libro) {
        try {
            String nuevoEstado = VALOR_ACTIVO.equals(libro.getEstado()) ? VALOR_INACTIVO : VALOR_ACTIVO;
            libroDao.updateStatus(libro.getId(), nuevoEstado);
            showAlert(Alert.AlertType.INFORMATION, "Estado actualizado", "El estado del libro se ha actualizado correctamente.");
            cargarLibros(); // Recargar la tabla para ver el cambio
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el estado", "Ocurrió un error al intentar actualizar el estado del libro en la base de datos.");
        }
    }


    // Método para el cierre de sesión
    @FXML
    private void onLogOut() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/ModalCerrarSesion.fxml"));
            Parent modalRoot = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("¿Cerrar sesión?");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();


            if (ModalCerrarSesionController.cerrarSesionConfirmado) {
                Stage currentStage = (Stage) btnLogOut.getScene().getWindow();
                currentStage.close();
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
                Parent loginRoot = loginLoader.load();

                Stage newStage = new Stage();
                newStage.setTitle("Inicio de sesión");
                newStage.setScene(new Scene(loginRoot));
                newStage.setMaximized(true);
                newStage.show();
            }

            ModalCerrarSesionController.cerrarSesionConfirmado = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irEstadisticas(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/estadisticasAdmin.fxml"));
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
    private void irBibliotecarios(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Bibliotecario.fxml"));
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



    // Método sobrecargado para mostrar alertas con 3 argumentos
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
