package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

    private UsuarioBibliotecaDaoImpl usuarioDao;
    private ObservableList<UsuarioBiblioteca> listaUsuarios;

    @FXML
    public void initialize() {
        usuarioDao = new UsuarioBibliotecaDaoImpl();
        configurarColumnasTabla();
        tableViewUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarUsuarios();
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

    @FXML
    private void onAddUsuario() {
        System.out.println("Abrir formulario para agregar usuario");
        // Implementar apertura de formulario para agregar usuario
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
