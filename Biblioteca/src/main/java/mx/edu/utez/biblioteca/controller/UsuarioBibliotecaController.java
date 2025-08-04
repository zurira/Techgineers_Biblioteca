package mx.edu.utez.biblioteca.controller;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.util.Duration;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalDate;
import java.util.Optional;

public class UsuarioBibliotecaController {

    @FXML
    private TableView<UsuarioBiblioteca> tableViewUsuarios;

    @FXML
    private TableColumn<UsuarioBiblioteca, Integer> colNo;

    @FXML
    private TableColumn<UsuarioBiblioteca, String> colNombreCompleto;

    @FXML
    private TableColumn<UsuarioBiblioteca, LocalDate> colFechaNacimiento;

    @FXML
    private TableColumn<UsuarioBiblioteca, String> colCorreo;

    @FXML
    private TableColumn<UsuarioBiblioteca, String> colTelefono;

    @FXML
    private TableColumn<UsuarioBiblioteca, String> colDireccion;

    @FXML
    private TableColumn<UsuarioBiblioteca, String> colEstado;

    @FXML
    private TableColumn<UsuarioBiblioteca, Void> colAcciones;

    @FXML
    private Label lblSinResultados;

    @FXML
    private TextField txtSearch;
    @FXML private Button btnlogout;

    private UsuarioBibliotecaDaoImpl usuarioDao;
    private ObservableList<UsuarioBiblioteca> listaUsuarios;

    @FXML
    public void initialize() {
        usuarioDao = new UsuarioBibliotecaDaoImpl();
        configurarColumnasTabla();
        tableViewUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarUsuarios();
        configurarFiltroBusqueda();
    }

    private void configurarColumnasTabla() {
        colNo.setCellFactory(column -> new TableCell<UsuarioBiblioteca, Integer>() {
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
        // Ajuste de anchura para cada columna
        colNo.setPrefWidth(70);
        colNombreCompleto.setPrefWidth(180);
        colFechaNacimiento.setPrefWidth(160);
        colCorreo.setPrefWidth(180);
        colTelefono.setPrefWidth(120);
        colDireccion.setPrefWidth(200);
        colEstado.setPrefWidth(100);
        colAcciones.setPrefWidth(90);
        colNombreCompleto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre() != null ? cellData.getValue().getNombre() : "N/A")
        );
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colCorreo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCorreo() != null ? cellData.getValue().getCorreo() : "N/A")
        );
        colTelefono.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTelefono() != null ? cellData.getValue().getTelefono() : "N/A")
        );
        colDireccion.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDireccion() != null ? cellData.getValue().getDireccion() : "N/A")
        );
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado() != null ? cellData.getValue().getEstado() : "N/A")
        );
        colEstado.setCellFactory(column -> new TableCell<UsuarioBiblioteca, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(item);
                    statusLabel.getStyleClass().clear();
                    if ("Activo".equals(item)) {
                        statusLabel.getStyleClass().add("status-activo");
                    } else if ("Inactivo".equals(item)) {
                        statusLabel.getStyleClass().add("status-inactivo");
                    }
                    HBox hbox = new HBox(statusLabel);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                    this.setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        colAcciones.setCellFactory(param -> new TableCell<UsuarioBiblioteca, Void>() {
            private final HBox buttons = new HBox(5);
            private final Button editButton = new Button();
            private final Button changeStatusButton = new Button();
            private final Button viewButton = new Button();
            {
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");
                editButton.setTooltip(new Tooltip("Editar usuario"));

                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button");
                viewButton.setTooltip(new Tooltip("Ver detalles"));

                // Alinea los botones a la izquierda
                buttons.setAlignment(Pos.CENTER_LEFT);
                buttons.getChildren().addAll(editButton, changeStatusButton, viewButton);

                editButton.setOnAction(event -> onEditUsuario(getTableView().getItems().get(getIndex())));
                viewButton.setOnAction(event -> onViewUsuario(getTableView().getItems().get(getIndex())));
                changeStatusButton.setOnAction(event -> onChangeStatus(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Alinea la celda completa a la izquierda
                    this.setAlignment(Pos.CENTER_LEFT);
                    UsuarioBiblioteca usuario = getTableView().getItems().get(getIndex());
                    FontIcon statusIcon;
                    if ("Activo".equals(usuario.getEstado())) {
                        statusIcon = new FontIcon("fa-toggle-on");
                        changeStatusButton.setTooltip(new Tooltip("Desactivar"));
                    } else {
                        statusIcon = new FontIcon("fa-toggle-off");
                        changeStatusButton.setTooltip(new Tooltip("Activar"));
                    }
                    statusIcon.getStyleClass().add("action-icon");
                    changeStatusButton.setGraphic(statusIcon);
                    changeStatusButton.getStyleClass().add("action-button");

                    setGraphic(buttons);
                }
            }
        });
    }

    private void cargarUsuarios() {
        try {
            listaUsuarios = FXCollections.observableArrayList(usuarioDao.findAll());
            tableViewUsuarios.setItems(listaUsuarios);
            lblSinResultados.setVisible(listaUsuarios.isEmpty());
            tableViewUsuarios.setVisible(!listaUsuarios.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "No se pudieron cargar los usuarios", "Hubo un error al obtener los datos. Revisa la conexión.");
        }
    }

    private void configurarFiltroBusqueda() {
        FilteredList<UsuarioBiblioteca> listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            String filtro = newValue.toLowerCase();

            listaFiltrada.setPredicate(usuario -> {
                if (filtro == null || filtro.isEmpty()) return true;

                boolean coincide =
                        usuario.getNombre().toLowerCase().contains(filtro) ||
                                (usuario.getCorreo() != null && usuario.getCorreo().toLowerCase().contains(filtro)) ||
                                (usuario.getTelefono() != null && usuario.getTelefono().toLowerCase().contains(filtro)) ||
                                (usuario.getDireccion() != null && usuario.getDireccion().toLowerCase().contains(filtro)) ||
                                (usuario.getFechaNacimiento() != null && usuario.getFechaNacimiento().toString().contains(filtro)) ||
                                (usuario.getEstado() != null && usuario.getEstado().toLowerCase().contains(filtro));

                return coincide;
            });

            lblSinResultados.setVisible(listaFiltrada.isEmpty());
        });

        SortedList<UsuarioBiblioteca> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tableViewUsuarios.comparatorProperty());
        tableViewUsuarios.setItems(listaOrdenada);
    }

    @FXML
    private void onAddUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarUsuario.fxml"));
            Parent root = loader.load();

            AgregarUsuarioController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Agregar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de agregar usuario.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void onEditUsuario(UsuarioBiblioteca usuario) {
        try {
            System.out.println("Editar usuario: " + usuario.getNombre());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/EditarUsuario.fxml"));
            Parent root = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.cargarUsuario(usuario);

            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarUsuarios();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error al editar", null, "No se pudo abrir el formulario.");
        }
    }

    private void onChangeStatus(UsuarioBiblioteca usuario) {
        String nuevoEstado = "Activo".equals(usuario.getEstado()) ? "Inactivo" : "Activo";
        String accion = "Activo".equals(nuevoEstado) ? "activar" : "desactivar";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText("¿Estás seguro de que quieres " + accion + " a este usuario?");
        confirmacion.setContentText("El usuario no se eliminará, solo se cambiará su estado a " + nuevoEstado + ".");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                usuario.setEstado(nuevoEstado);
                usuarioDao.update(usuario);
                mostrarAlertaInformacion("Éxito", "Estado actualizado", "El estado del usuario se ha actualizado correctamente.");
                cargarUsuarios();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlertaError("Error de Actualización", "No se pudo actualizar el estado", "Hubo un error de conexión con la base de datos.");
            }
        }
    }

    private void onViewUsuario(UsuarioBiblioteca usuario) {
        System.out.println("Ver detalles de usuario: " + usuario.getNombre());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/VerUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Verr Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error al ver", null, "No se pudo abrir el formulario.");
        }
    }

    @FXML
    private void irPrestamos(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Prestamo.fxml"));
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

    private void mostrarAlertaError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void mostrarAlertaInformacion(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}