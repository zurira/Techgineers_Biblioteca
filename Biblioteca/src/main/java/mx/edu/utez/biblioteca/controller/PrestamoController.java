package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.IOException;
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
    private TableColumn<Prestamo, Void> colAcciones;

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
        configurarFiltroBusqueda();

        tableViewPrestamos.getItems().addListener((ListChangeListener<Prestamo>) c -> tableViewPrestamos.refresh());
        tableViewPrestamos.sortPolicyProperty().set(tv -> {
            boolean sorted = TableView.DEFAULT_SORT_POLICY.call(tv);
            tv.refresh();
            return sorted;
        });
    }

    private void configurarColumnasTabla() {
        // Ajuste de ancho de las columnas
        colNo.setPrefWidth(40);
        colNombreUsuario.setPrefWidth(150);
        colTituloLibro.setPrefWidth(200);
        colFechaPrestamo.setPrefWidth(120);
        colFechaLimite.setPrefWidth(120);
        colFechaReal.setPrefWidth(120);
        colEstado.setPrefWidth(100);
        colAcciones.setPrefWidth(100);

        colNo.setCellFactory(column -> new TableCell<Prestamo, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colNombreUsuario.setCellValueFactory(cellData -> {
            UsuarioBiblioteca usuario = cellData.getValue().getUsuario();
            return new SimpleStringProperty(usuario != null ? usuario.getNombre() : "N/A");
        });

        colTituloLibro.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue().getLibro();
            return new SimpleStringProperty(libro != null ? libro.getTitulo() : "N/A");
        });

        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaLimite.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        colFechaReal.setCellValueFactory(new PropertyValueFactory<>("fechaReal"));

        // Bloque de código para la columna 'Estado'
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> {
            return new TableCell<Prestamo, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Label statusLabel = new Label(item);
                        statusLabel.getStyleClass().add("status-label");

                        switch (item) {
                            case "Finalizado":
                                statusLabel.getStyleClass().add("status-finalizado");
                                break;
                            case "Retrasado":
                                statusLabel.getStyleClass().add("status-retrasado");
                                break;
                            case "Activo":
                                statusLabel.getStyleClass().add("status-activo");
                                break;
                            default:
                                break;
                        }
                        HBox container = new HBox(statusLabel);
                        container.setAlignment(Pos.CENTER_LEFT); // Alineación a la izquierda
                        setGraphic(container);
                        this.setAlignment(Pos.CENTER_LEFT); // Alineación de la celda
                        setText(null);
                    }
                }
            };
        });

        // Bloque de código para la columna 'Acciones'
        colAcciones.setCellValueFactory(param -> null);
        colAcciones.setCellFactory(param -> new TableCell<Prestamo, Void>() {
            private final Button editButton = new Button();
            private final Button viewButton = new Button();

            {
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar préstamo"));

                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button");
                viewButton.setTooltip(new Tooltip("Ver detalles"));

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
                    buttons.setAlignment(Pos.CENTER_LEFT); // Alineación de los botones a la izquierda
                    this.setAlignment(Pos.CENTER_LEFT); // Alineación de la celda
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarPrestamos() {
        try {
            listaPrestamos = FXCollections.observableArrayList(prestamoDao.findAll());
            tableViewPrestamos.setItems(listaPrestamos);
            lblSinResultados.setVisible(listaPrestamos.isEmpty());
            tableViewPrestamos.setVisible(!listaPrestamos.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los préstamos");
            alert.setContentText("Hubo un error al intentar obtener los datos de los préstamos. Por favor, revisa la conexión a la base de datos.");
            alert.showAndWait();
        }
    }

    private void configurarFiltroBusqueda() {
        FilteredList<Prestamo> listaFiltrada = new FilteredList<>(listaPrestamos, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            String filtro = newValue.toLowerCase();
            listaFiltrada.setPredicate(prestamo -> {
                if (filtro == null || filtro.isEmpty()) return true;

                String nombreUsuario = prestamo.getUsuario() != null ? prestamo.getUsuario().getNombre().toLowerCase() : "";
                String tituloLibro = prestamo.getLibro() != null ? prestamo.getLibro().getTitulo().toLowerCase() : "";
                String estado = prestamo.getEstado() != null ? prestamo.getEstado().toLowerCase() : "";

                return nombreUsuario.contains(filtro) ||
                        tituloLibro.contains(filtro) ||
                        estado.contains(filtro);
            });

            lblSinResultados.setVisible(listaFiltrada.isEmpty());
        });

        SortedList<Prestamo> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tableViewPrestamos.comparatorProperty());
        tableViewPrestamos.setItems(listaOrdenada);
    }

    @FXML
    private void onAddPrestamo(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarPrestamo.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Nuevo Préstamo");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root, 1000, 600));
            modalStage.showAndWait();
            cargarPrestamos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onEditPrestamo(Prestamo prestamo, ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editarPrestamo.fxml"));
            Parent root = loader.load();
            EditarPrestamoController controller = loader.getController();
            controller.inicializar(prestamo);
            Stage modalStage = new Stage();
            modalStage.setTitle("Editar Préstamo");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root, 1000, 600));
            modalStage.showAndWait();
            cargarPrestamos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onViewPrestamo(Prestamo prestamo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/VerPrestamo.fxml"));
            Parent root = loader.load();

            VerPrestamoController controller = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));

            controller.setDialogStage(dialogStage);
            controller.setPrestamo(prestamo);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista de detalles del préstamo.");
            alert.setContentText("Hubo un error al intentar abrir la ventana de detalles. Por favor, verifica el archivo FXML.");
            alert.showAndWait();
        }
    }
}