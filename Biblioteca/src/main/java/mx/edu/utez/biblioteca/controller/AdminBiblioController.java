package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.BibliotecarioDaoImpl;
import mx.edu.utez.biblioteca.model.Bibliotecario;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import org.kordamp.ikonli.javafx.FontIcon;

public class AdminBiblioController {

    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnAddBibliotecario;
    @FXML
    private TableView<Bibliotecario> tableViewBibliotecarios;
    @FXML
    private TableColumn<Bibliotecario, Void> colNo;
    @FXML
    private TableColumn<Bibliotecario, String> colNombre;
    @FXML
    private TableColumn<Bibliotecario, String> colCorreo;
    @FXML
    private TableColumn<Bibliotecario, String> colTelefono;
    @FXML
    private TableColumn<Bibliotecario, String> colUsername;
    @FXML
    private TableColumn<Bibliotecario, String> colRol;
    @FXML
    private TableColumn<Bibliotecario, String> colEstado;
    @FXML
    private TableColumn<Bibliotecario, Void> colAcciones;
    @FXML
    private Label lblSinResultados;
    @FXML
    private ProgressIndicator progressIndicator; // Nuevo fx:id para el indicador de carga

    private BibliotecarioDaoImpl bibliotecarioDao;
    private ObservableList<Bibliotecario> listaBibliotecarios;

    @FXML
    public void initialize() {
        bibliotecarioDao = new BibliotecarioDaoImpl();
        configurarColumnasTabla();
        tableViewBibliotecarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarBibliotecarios();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarBibliotecarios(newValue);
        });

        if (btnLogOut != null) {
            btnLogOut.setOnAction(event -> onLogOut());
        }
    }

    private void configurarColumnasTabla() {
        colNo.setCellFactory(column -> new TableCell<Bibliotecario, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        colRol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRol() != null) {
                return new SimpleStringProperty(cellData.getValue().getRol().getNombre());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        colEstado.setCellValueFactory(cellData -> {
            if ("S".equals(cellData.getValue().getEstado())) {
                return new SimpleStringProperty("Activo");
            } else {
                return new SimpleStringProperty("Inactivo");
            }
        });
        colEstado.setCellFactory(column -> new TableCell<Bibliotecario, String>() {
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
        });

        colAcciones.setCellFactory(param -> new TableCell<Bibliotecario, Void>() {
            private final HBox buttons = new HBox(5);
            private final Button editButton = new Button();
            private final Button changeStatusButton = new Button();
            private final Button viewButton = new Button();

            {
                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button");
                viewButton.setTooltip(new Tooltip("Ver detalles"));

                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar bibliotecario"));

                buttons.setAlignment(Pos.CENTER_LEFT);
                buttons.getChildren().addAll(viewButton, editButton, changeStatusButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Bibliotecario bibliotecario = getTableView().getItems().get(getIndex());

                    FontIcon statusIcon;
                    if ("S".equals(bibliotecario.getEstado())) {
                        statusIcon = new FontIcon("fa-toggle-on");
                        changeStatusButton.setTooltip(new Tooltip("Desactivar"));
                    } else {
                        statusIcon = new FontIcon("fa-toggle-off");
                        changeStatusButton.setTooltip(new Tooltip("Activar"));
                    }
                    statusIcon.getStyleClass().add("action-icon");
                    changeStatusButton.setGraphic(statusIcon);
                    changeStatusButton.getStyleClass().add("action-button");

                    viewButton.setOnAction(event -> onViewBibliotecario(bibliotecario));
                    editButton.setOnAction(event -> onEditBibliotecario(bibliotecario));
                    changeStatusButton.setOnAction(event -> onChangeStatus(bibliotecario));

                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarBibliotecarios() {
        try {
            listaBibliotecarios = FXCollections.observableArrayList(bibliotecarioDao.findAll());
            listaBibliotecarios.sort(Comparator.comparing(Bibliotecario::getId));
            tableViewBibliotecarios.setItems(listaBibliotecarios);
            lblSinResultados.setVisible(listaBibliotecarios.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los bibliotecarios",
                    "Hubo un error al intentar obtener los datos. Por favor, revisa la conexión a la base de datos.");
        }
    }

    private void filtrarBibliotecarios(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            tableViewBibliotecarios.setItems(listaBibliotecarios);
            lblSinResultados.setVisible(false);
            tableViewBibliotecarios.refresh();
            return;
        }

        String filtroLower = filtro.toLowerCase();
        ObservableList<Bibliotecario> bibliotecariosFiltrados = FXCollections.observableArrayList();

        for (Bibliotecario bibliotecario : listaBibliotecarios) {
            String nombre = bibliotecario.getNombre() != null ? bibliotecario.getNombre().toLowerCase() : "";
            String correo = bibliotecario.getCorreo() != null ? bibliotecario.getCorreo().toLowerCase() : "";
            String username = bibliotecario.getUsername() != null ? bibliotecario.getUsername().toLowerCase() : "";
            String rolNombre = bibliotecario.getRol() != null ? bibliotecario.getRol().getNombre().toLowerCase() : "";

            if (nombre.contains(filtroLower) || correo.contains(filtroLower) ||
                    username.contains(filtroLower) || rolNombre.contains(filtroLower)) {
                bibliotecariosFiltrados.add(bibliotecario);
            }
        }

        tableViewBibliotecarios.setItems(bibliotecariosFiltrados);
        lblSinResultados.setVisible(bibliotecariosFiltrados.isEmpty());
    }

    @FXML
    private void onAddBibliotecario() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarBibliotecario.fxml"));
            Parent root = fxmlLoader.load();

            ModalAgregarBibliotecarioController modalController = fxmlLoader.getController();

            Stage stage = new Stage();
            stage.setTitle("Agregar Bibliotecario");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (modalController.seAgregoUsuario()) {
                cargarBibliotecarios();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario.", "Hubo un error al intentar cargar la vista de agregar bibliotecario.");
        }
    }

    private void onEditBibliotecario(Bibliotecario bibliotecario) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editarBibliotecario.fxml"));
            Parent root = fxmlLoader.load();

            EditarBibliotecarioController controller = fxmlLoader.getController();
            controller.setBibliotecario(bibliotecario);

            Stage stage = new Stage();
            stage.setTitle("Editar Bibliotecario");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            cargarBibliotecarios();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir el formulario.", "Hubo un error al intentar cargar la vista de agregar bibliotecario.");
        }
    }

    private void onViewBibliotecario(Bibliotecario bibliotecarioSeleccionado) {
        // Muestra el indicador de carga y deshabilita la tabla para evitar interacción
        progressIndicator.setVisible(true);
        tableViewBibliotecarios.setDisable(true);

        Task<Bibliotecario> loadBibliotecarioTask = new Task<Bibliotecario>() {
            @Override
            protected Bibliotecario call() throws Exception {
                // Tarea que se ejecuta en segundo plano para cargar los datos completos
                return bibliotecarioDao.findById(bibliotecarioSeleccionado.getId());
            }
        };

        loadBibliotecarioTask.setOnSucceeded(event -> {
            // Se ejecuta en el hilo principal de la UI cuando la tarea termina con éxito
            progressIndicator.setVisible(false);
            tableViewBibliotecarios.setDisable(false);

            Bibliotecario bibliotecarioConFoto = loadBibliotecarioTask.getValue();

            if (bibliotecarioConFoto == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Bibliotecario no encontrado", "No se pudo encontrar la información completa del bibliotecario.");
                return;
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/VerBibliotecario.fxml"));
                Parent root = fxmlLoader.load();
                VerBibliotecarioController controller = fxmlLoader.getController();
                controller.setBibliotecario(bibliotecarioConFoto);

                Stage stage = new Stage();
                stage.setTitle("Detalles del Bibliotecario");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudo abrir la vista de detalles.", "Hubo un error al intentar cargar la vista del bibliotecario.");
            }
        });

        loadBibliotecarioTask.setOnFailed(event -> {
            // Se ejecuta en el hilo principal de la UI si la tarea falla
            progressIndicator.setVisible(false);
            tableViewBibliotecarios.setDisable(false);
            Throwable e = loadBibliotecarioTask.getException();
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de Datos", "No se pudo obtener el bibliotecario", "Hubo un error al intentar cargar los datos del bibliotecario: " + e.getMessage());
        });

        // Iniciar la tarea en un nuevo hilo
        new Thread(loadBibliotecarioTask).start();
    }

    private void onChangeStatus(Bibliotecario bibliotecario) {
        String nuevoEstado = "S".equals(bibliotecario.getEstado()) ? "N" : "S";
        String accion = "S".equals(nuevoEstado) ? "activar" : "desactivar";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cambio de estado");
        alert.setHeaderText("¿Estás seguro de que quieres " + accion + " a " + bibliotecario.getNombre() + "?");
        alert.setContentText("Esta acción cambiará el estado del bibliotecario.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (bibliotecarioDao.updateStatus(bibliotecario.getId(), nuevoEstado)) {
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Estado actualizado", "El estado del bibliotecario se ha actualizado correctamente.");
                    cargarBibliotecarios();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el estado", "Hubo un problema al intentar actualizar el estado del bibliotecario.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error de Actualización", "No se pudo actualizar el estado", "Hubo un error de conexión con la base de datos.");
            }
        }
    }

    @FXML
    private void onLogOut() {
        System.out.println("Cerrar sesión");
        showAlert(Alert.AlertType.INFORMATION, "Cerrar Sesión", "Funcionalidad de Cierre de Sesión", "Aquí se implementará la lógica para cerrar la sesión y regresar a la pantalla de inicio de sesión.");
    }

    @FXML
    private void irLibros(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AdminDashboard.fxml"));
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}