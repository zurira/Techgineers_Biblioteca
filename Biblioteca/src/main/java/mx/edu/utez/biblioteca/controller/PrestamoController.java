package mx.edu.utez.biblioteca.controller;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Pos;
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
    @FXML private Button btnlogout;

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

        tableViewPrestamos.getItems().addListener((ListChangeListener<Prestamo>) c -> tableViewPrestamos.refresh());
        tableViewPrestamos.sortPolicyProperty().set(tv -> {
            boolean sorted = TableView.DEFAULT_SORT_POLICY.call(tv);
            tv.refresh();
            return sorted;
        });
    }

    private void configurarColumnasTabla() {

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
                        container.setAlignment(Pos.CENTER);
                        setGraphic(container);
                        setText(null);
                    }
                }
            };
        });

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
            listaPrestamos = FXCollections.observableArrayList(prestamoDao.findAll());
            tableViewPrestamos.setItems(listaPrestamos);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los préstamos");
            alert.setContentText("Hubo un error al intentar obtener los datos de los préstamos. Por favor, revisa la conexión a la base de datos.");
            alert.showAndWait();
        }
    }

    private void filtrarPrestamos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tableViewPrestamos.setItems(listaPrestamos);
            lblSinResultados.setVisible(false);
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

    @FXML
    private void irEstadisticas(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Estadisticas.fxml"));
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
    private void irUsuarios(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Usuarios.fxml"));
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
    private void cerrarSesion() {
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
                Stage currentStage = (Stage) btnlogout.getScene().getWindow();
                currentStage.close();
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
                Parent loginRoot = loginLoader.load();

                Stage newStage = new Stage();
                newStage.setTitle("Inicio de sesión");
                newStage.setScene(new Scene(loginRoot));
                newStage.setMaximized(true); //
                newStage.show();
            }

            ModalCerrarSesionController.cerrarSesionConfirmado = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}