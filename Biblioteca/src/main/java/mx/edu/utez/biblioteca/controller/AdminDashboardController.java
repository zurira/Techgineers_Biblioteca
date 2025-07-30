package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
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

    @FXML
    private TableColumn<Libro, Void> colAcciones;

    @FXML
    private Label lblSinResultados;

    private LibroDaoImpl libroDao;
    private ObservableList<Libro> listaLibros;

    @FXML
    public void initialize() {
        libroDao = new LibroDaoImpl();
        configurarColumnasTabla();
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
        colNo.setCellValueFactory(new PropertyValueFactory<>("id"));
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

        colResumen.setCellValueFactory(new PropertyValueFactory<>("resumen"));

        colCategoria.setCellValueFactory(cellData -> {
            Categoria categoria = cellData.getValue().getCategoria();
            return new SimpleStringProperty(categoria != null ? categoria.getNombre() : "N/A");
        });

        colAnioPublicacion.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));

        // Columna de Acciones (Editar y estado del libro)
        colAcciones.setCellValueFactory(param -> null);
        colAcciones.setCellFactory(param -> new TableCell<Libro, Void>() {
            private final Button editButton = new Button();
            private final Label statusLabel = new Label(); // Usamos un Label en lugar de CheckBox

            {
                // Botón para editar
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar libro"));

                editButton.setOnAction(event -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    onEditLibro(libro);
                });

                // Configuración del Label de estado del libro
                statusLabel.getStyleClass().add("status-label");
                statusLabel.setAlignment(Pos.CENTER);
                statusLabel.setPrefWidth(70);
                statusLabel.setMaxWidth(Double.MAX_VALUE);
                statusLabel.setPadding(new Insets(5, 10, 5, 10)); // Padding para el Label
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro libro = getTableView().getItems().get(getIndex());

                    // Actualizar el texto y el estilo del Label de estado
                    if ("A".equals(libro.getEstado())) {
                        statusLabel.setText("Activo");
                        statusLabel.getStyleClass().remove("status-inactive");
                        statusLabel.getStyleClass().add("status-active");
                    } else {
                        statusLabel.setText("Inactivo");
                        statusLabel.getStyleClass().remove("status-active");
                        statusLabel.getStyleClass().add("status-inactive");
                    }

                    HBox buttons = new HBox(5, editButton, statusLabel); // Contiene el botón de edición y el Label
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarLibros() {
        try {
            listaLibros = FXCollections.observableArrayList(libroDao.findAll());
            tableViewLibros.setItems(listaLibros);
            lblSinResultados.setVisible(listaLibros.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar libros: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los libros",
                    "Hubo un error al intentar obtener los datos de los libros. Por favor, revisa la conexión a la base de datos.");
        }
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
        System.out.println("Agregar nuevo libro");
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidad", "Agregar Libro", "Aquí se abrirá la ventana para agregar un nuevo libro.");
    }
    // Método para editar libro

    private void onEditLibro(Libro libro) {
        System.out.println("Editar libro: " + libro.getId());
        // Modal para implementar la lógica para abrir el formulario de edición con los datos del libro
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidad", "Editar Libro", "Aquí se abrirá la ventana para editar el libro con ID: " + libro.getId() + " - " + libro.getTitulo() + "\nEstado actual: " + ("A".equals(libro.getEstado()) ? "Activo" : "Inactivo"));

    }

    // Método para el cierre de sesión
    @FXML
    private void onLogOut() {
        System.out.println("Cerrar sesión");
        // Modal para implementar la lógica para cerrar la sesión
        showAlert(Alert.AlertType.INFORMATION, "Cerrar Sesión", "Funcionalidad de Cierre de Sesión", "Aquí se implementará la lógica para cerrar la sesión y regresar a la pantalla de inicio de sesión.");
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}