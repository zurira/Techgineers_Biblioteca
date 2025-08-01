package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mx.edu.utez.biblioteca.dao.impl.BibliotecarioDaoImpl;
import mx.edu.utez.biblioteca.model.Bibliotecario;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.Optional;

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
        colNo.setCellFactory(column -> {
            return new TableCell<Bibliotecario, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        // Si la celda está vacía, no mostrar nada
                        setText(null);
                    } else {
                        // Muestra el número de fila, empezando desde 1
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            };
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
        colEstado.setCellFactory(column -> {
            return new TableCell<Bibliotecario, String>() {
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

                        // Centra el contenido del Label dentro de la celda
                        this.setAlignment(Pos.CENTER);

                        // Establece el Label como el gráfico de la celda
                        setGraphic(label);
                        setText(null); // No mostrar el texto directamente en la celda
                    }
                }
            };
        });



        colAcciones.setCellFactory(param -> new TableCell<Bibliotecario, Void>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button changeStatusButton = new Button();

            {
                // Botón de edición
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar bibliotecario"));

                editButton.setOnAction(event -> {
                    Bibliotecario bibliotecario = getTableView().getItems().get(getIndex());
                    onEditBibliotecario(bibliotecario);
                });

                // Botón de cambio de estado
                FontIcon statusIcon = new FontIcon("fa-eye-slash");
                statusIcon.getStyleClass().add("action-icon");
                changeStatusButton.setGraphic(statusIcon);
                changeStatusButton.getStyleClass().add("action-button");
                changeStatusButton.setTooltip(new Tooltip("Cambiar estado"));

                changeStatusButton.setOnAction(event -> {
                    Bibliotecario bibliotecario = getTableView().getItems().get(getIndex());
                    onChangeStatus(bibliotecario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, changeStatusButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarBibliotecarios() {
        try {
            listaBibliotecarios = FXCollections.observableArrayList(bibliotecarioDao.findAll());
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
        System.out.println("Agregar nuevo bibliotecario");
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidad", "Agregar Bibliotecario", "Aquí se abrirá la ventana para agregar un nuevo bibliotecario.");
    }

    private void onEditBibliotecario(Bibliotecario bibliotecario) {
        System.out.println("Editar bibliotecario: " + bibliotecario.getId());
        showAlert(Alert.AlertType.INFORMATION, "Funcionalidad", "Editar Bibliotecario", "Aquí se abrirá la ventana para editar al bibliotecario con ID: " + bibliotecario.getId() + " - " + bibliotecario.getNombre());
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
                    cargarBibliotecarios(); // Recargar la tabla
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

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}