package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import java.time.LocalDate;
import java.util.Optional;

public class PrestamoController {

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnAddPrestamo;

    @FXML
    private TableView<Prestamo> tableViewPrestamos;

    @FXML
    private TableColumn<Prestamo, Integer> colNo;

    @FXML
    private TableColumn<Prestamo, String> colNombreUsuario;

    @FXML
    private TableColumn<Prestamo, String> colTituloLibro;

    @FXML
    private TableColumn<Prestamo, LocalDate> colFechaPrestamo;

    @FXML
    private TableColumn<Prestamo, LocalDate> colFechaLimite;

    @FXML
    private TableColumn<Prestamo, LocalDate> colFechaReal;

    @FXML
    private TableColumn<Prestamo, String> colEstado;

    @FXML
    private TableColumn<Prestamo, Void> colAcciones; // Para los botones de acciones

    @FXML
    private Label lblSinResultados;

    private PrestamoDaoImpl prestamoDao;
    private ObservableList<Prestamo> listaPrestamos;


    @FXML
    public void initialize() {
        prestamoDao = new PrestamoDaoImpl();
        configurarColumnasTabla();
        tableViewPrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarPrestamos();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPrestamos(newValue);
        });
    }

    private void configurarColumnasTabla() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Configuración para el nombre del usuario, con manejo de nulidad
        colNombreUsuario.setCellValueFactory(cellData -> {
            UsuarioBiblioteca usuario = cellData.getValue().getUsuario();
            // Si el usuario es null, muestra "N/A" o un string vacío.
            return new SimpleStringProperty(usuario != null ? usuario.getNombre() : "N/A");
        });

        // Configuración para el título del libro, con manejo de nulidad
        colTituloLibro.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue().getLibro();
            // Si el libro es null, muestra "N/A" o un string vacío.
            return new SimpleStringProperty(libro != null ? libro.getTitulo() : "N/A");
        });

        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaLimite.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        colFechaReal.setCellValueFactory(new PropertyValueFactory<>("fechaReal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));


        // Configuración de la columna de Acciones con Ikonli
        colAcciones.setCellValueFactory(param -> null);
        colAcciones.setCellFactory(param -> new TableCell<Prestamo, Void>() {
            private final Button editButton = new Button();
            private final Button viewButton = new Button();

            {
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");

                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button");


                // Manejadores de eventos para los botones
                editButton.setOnAction(event -> {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    onEditPrestamo(prestamo, event);
                });

                viewButton.setOnAction(event -> {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    onViewPrestamo(prestamo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, viewButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarPrestamos() {
        try {
            listaPrestamos = FXCollections.observableArrayList(prestamoDao.findAll()); // ¡CAMBIO: Usar findAll() de la interfaz!
            tableViewPrestamos.setItems(listaPrestamos);
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza completa del error para depuración
            System.err.println("Error al cargar préstamos: " + e.getMessage());
            // Considera mostrar un Alert al usuario aquí si el error es crítico
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los préstamos");
            alert.setContentText("Hubo un error al intentar obtener los datos de los préstamos. Por favor, revisa la conexión a la base de datos.");
            alert.showAndWait();
        }
    }

    private void filtrarPrestamos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tableViewPrestamos.setItems(listaPrestamos); // Mostrar todos
            lblSinResultados.setVisible(false);  // Ocultar mensaje cuando no hay búsqueda
            return;
        }

        String filtroLower = filtro.toLowerCase();

        ObservableList<Prestamo> prestamosFiltrados = FXCollections.observableArrayList();

        for (Prestamo p : listaPrestamos) {
            String nombreUsuario = p.getUsuario() != null ? p.getUsuario().getNombre().toLowerCase() : "";
            String tituloLibro = p.getLibro() != null ? p.getLibro().getTitulo().toLowerCase() : "";
            String estado = p.getEstado() != null ? p.getEstado().toLowerCase() : "";

            if (nombreUsuario.contains(filtroLower) || tituloLibro.contains(filtroLower) || estado.contains(filtroLower)) {
                prestamosFiltrados.add(p);
            }
        }

        tableViewPrestamos.setItems(prestamosFiltrados);
        lblSinResultados.setVisible(prestamosFiltrados.isEmpty());
    }


    @FXML
    private void onAddPrestamo(ActionEvent e) {
        System.out.println("Agregar nuevo préstamo");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarPrestamo.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Nuevo Préstamo");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root, 1000, 600));

            modalStage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onEditPrestamo(Prestamo prestamo, ActionEvent e) {
        Prestamo prestamoSeleccionado = tableViewPrestamos.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editarPrestamo.fxml"));
            Parent root = loader.load();

            EditarPrestamoController controller = loader.getController();
            controller.inicializar(prestamo);

            // Crear nuevo Stage para que sea modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Préstamo");
            modalStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root, 1000, 600)); // Puedes ajustar tamaño

            modalStage.showAndWait(); // Espera a que se cierre
            //cargarPrestamos();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void onViewPrestamo(Prestamo prestamo) {
        System.out.println("Ver detalles del préstamo: " + prestamo.getId());
        // Implementación de la lógica para ver detalles del préstamo
    }
}