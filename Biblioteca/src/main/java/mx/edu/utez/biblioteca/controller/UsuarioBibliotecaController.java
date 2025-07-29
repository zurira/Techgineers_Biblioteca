package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
        colNo.setCellValueFactory(new PropertyValueFactory<>("id"));

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

        colAcciones.setCellValueFactory(param -> null);
        colAcciones.setCellFactory(param -> new TableCell<UsuarioBiblioteca, Void>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button viewButton = new Button();

            {
                FontIcon editIcon = new FontIcon("fa-pencil");
                editIcon.getStyleClass().add("action-icon");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");

                FontIcon deleteIcon = new FontIcon("fa-trash");
                deleteIcon.getStyleClass().add("action-icon");
                deleteButton.setGraphic(deleteIcon);
                deleteButton.getStyleClass().add("action-button");

                FontIcon viewIcon = new FontIcon("fa-eye");
                viewIcon.getStyleClass().add("action-icon");
                viewButton.setGraphic(viewIcon);
                viewButton.getStyleClass().add("action-button");

                editButton.setOnAction(event -> {
                    UsuarioBiblioteca usuario = getTableView().getItems().get(getIndex());
                    onEditUsuario(usuario);
                });

                deleteButton.setOnAction(event -> {
                    UsuarioBiblioteca usuario = getTableView().getItems().get(getIndex());
                    onDeleteUsuario(usuario);
                });

                viewButton.setOnAction(event -> {
                    UsuarioBiblioteca usuario = getTableView().getItems().get(getIndex());
                    onViewUsuario(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton, viewButton);
                    buttons.setAlignment(Pos.CENTER);
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
                                usuario.getFechaNacimiento().toString().contains(filtro) ||
                                (usuario.getEstado().equalsIgnoreCase("S") && "activo".contains(filtro)) ||
                                (usuario.getEstado().equalsIgnoreCase("N") && "inactivo".contains(filtro));

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
        System.out.println("Abrir formulario para agregar usuario");
        // Implementar apertura de formulario para agregar usuario
        @FXML
        private void onAddUsuario() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AgregarUsuario.fxml"));
                Parent root = loader.load();

                AgregarUsuarioController controller = loader.getController();

                // Mostrar el modal
                Stage stage = new Stage();
                stage.setTitle("Agregar Usuario");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                // Si se guardó correctamente, recargar la tabla
                if (controller.isGuardado()) {
                    cargarUsuarios(); // Método que recarga los usuarios desde la BD
                }

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo abrir el formulario de agregar usuario.");
            }
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
        System.out.println("Editar usuario: " + usuario.getNombre());
        // Implementar apertura de formulario para editar usuario
    }

    private void onDeleteUsuario(UsuarioBiblioteca usuario) {
        // No se elimina el usuario (de debe desactivar) checar eso
    }

    private void onViewUsuario(UsuarioBiblioteca usuario) {
        System.out.println("Ver detalles de usuario: " + usuario.getNombre());
        // Implementar vista de detalles del usuario
    }

    private void mostrarAlertaError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}