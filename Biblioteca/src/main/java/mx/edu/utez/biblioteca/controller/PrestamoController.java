package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.ConfiguracionDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;

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
    @FXML
    private Button btnlogout;
    @FXML
    private TableColumn<Prestamo, String> colMulta;
    @FXML
    private TextField txtTarifaMulta;

    @FXML
    private ChoiceBox<String> choiceBoxEstado;

    private PrestamoDaoImpl prestamoDao;
    private ObservableList<Prestamo> listaPrestamos;
    private ObservableList<Prestamo> listaPrestamosOriginal;
    private double tarifaMultaActual;

    private String filtroEstadoActual = "Todos";

    @FXML
    public void initialize() {
        prestamoDao = new PrestamoDaoImpl();
        configurarColumnasTabla();
        tableViewPrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Carga inicial de datos
        cargarPrestamos();

        // Filtro de búsqueda
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });

        // Configuración y filtro del ChoiceBox
        choiceBoxEstado.setItems(FXCollections.observableArrayList("Estado", "Activo", "Finalizado", "Retrasado"));
        choiceBoxEstado.getSelectionModel().selectFirst();
        choiceBoxEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // En esta línea se valida si la opción seleccionada es "Estado" para no filtrar
            filtroEstadoActual = "Estado".equals(newValue) ? "Todos" : newValue;
            aplicarFiltros();
        });

        try {
            ConfiguracionDaoImpl configDao = new ConfiguracionDaoImpl();
            double tarifaActual = configDao.obtenerTarifaMulta();
            txtTarifaMulta.setText(String.valueOf(tarifaActual));
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar la tarifa actual: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
            // Se obtiene el objeto Libro directamente del Prestamo
            Libro libro = cellData.getValue().getLibro();
            return new SimpleStringProperty(libro != null ? libro.getTitulo() : "N/A");
        });

        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaLimite.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        colFechaReal.setCellValueFactory(new PropertyValueFactory<>("fechaReal"));

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
                        container.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(container);
                        setText(null);
                    }
                }
            };
        });
        try {
            ConfiguracionDaoImpl configDao = new ConfiguracionDaoImpl();
            tarifaMultaActual = configDao.obtenerTarifaMulta();
        } catch (SQLException e) {
            tarifaMultaActual = 0.0;
            e.printStackTrace();
        }

        colMulta.setCellValueFactory(cellData -> {
            Prestamo p = cellData.getValue();
            double multa = p.calcularMulta(tarifaMultaActual);
            return new SimpleStringProperty(String.format("$%.2f", multa));
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
                    buttons.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarPrestamos() {
        try {
            listaPrestamosOriginal = FXCollections.observableArrayList(prestamoDao.findAll());
            listaPrestamosOriginal.sort(Comparator.comparing(Prestamo::getId).reversed());

            LocalDate hoy = LocalDate.now();
            for (Prestamo p : listaPrestamosOriginal) {
                double tarifa = tarifaMultaActual;
                String nuevoEstado = p.calcularEstado(hoy, tarifa);
                p.setEstado(nuevoEstado);
            }

            listaPrestamos = FXCollections.observableArrayList(listaPrestamosOriginal);
            tableViewPrestamos.setItems(listaPrestamos);
            tableViewPrestamos.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los préstamos");
            alert.setContentText("Hubo un error al intentar obtener los datos de los préstamos. Por favor, revisa la conexión a la base de datos.");
            alert.showAndWait();
        }
    }

    private void aplicarFiltros() {
        String filtroTexto = txtSearch.getText().toLowerCase();

        ObservableList<Prestamo> prestamosFiltrados = FXCollections.observableArrayList();

        for (Prestamo p : listaPrestamosOriginal) {
            String nombreUsuario = p.getUsuario() != null ? p.getUsuario().getNombre().toLowerCase() : "";
            String tituloLibro = p.getLibro() != null ? p.getLibro().getTitulo().toLowerCase() : "";
            String estadoPrestamo = p.getEstado() != null ? p.getEstado() : "";

            boolean coincideTexto = nombreUsuario.contains(filtroTexto) || tituloLibro.contains(filtroTexto);
            boolean coincideEstado = "Todos".equals(filtroEstadoActual) || estadoPrestamo.equals(filtroEstadoActual);

            if (coincideTexto && coincideEstado) {
                prestamosFiltrados.add(p);
            }
        }
        tableViewPrestamos.setItems(prestamosFiltrados);
        lblSinResultados.setVisible(prestamosFiltrados.isEmpty());
        tableViewPrestamos.refresh();
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
            aplicarFiltros();
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
            aplicarFiltros();
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
                newStage.setMaximized(true);
                newStage.show();
            }

            ModalCerrarSesionController.cerrarSesionConfirmado = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarTarifaMulta() {
        try {
            String textoTarifa = txtTarifaMulta.getText();
            double nuevaTarifa = Double.parseDouble(textoTarifa);
            if (nuevaTarifa < 0) {
                mostrarAlerta("La tarifa no puede ser negativa.", Alert.AlertType.WARNING);
                return;
            }

            ConfiguracionDaoImpl configDao = new ConfiguracionDaoImpl();
            configDao.actualizarTarifaMulta(nuevaTarifa);
            tarifaMultaActual = nuevaTarifa;

            LocalDate hoy = LocalDate.now();
            for (Prestamo p : listaPrestamosOriginal) {
                double multa = p.calcularMulta(tarifaMultaActual);
                p.setMulta(multa);
                String nuevoEstado = p.calcularEstado(hoy, tarifaMultaActual);
                p.setEstado(nuevoEstado);
            }
            tableViewPrestamos.refresh();
            aplicarFiltros();
            mostrarAlerta("Tarifa actualizada correctamente.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor ingresa un número válido.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            mostrarAlerta("Error al guardar la tarifa: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}