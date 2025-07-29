package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.biblioteca.model.Usuario;
import java.util.List;
import org.kordamp.ikonli.javafx.FontIcon;

public class SuperAdminController {
    @FXML private TableView<Usuario> adminTable;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarAdministradores();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> buscarAdministradores(newValue));

        logoutButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("¿Cerrar sesión?");
            alert.setContentText("¿Estás segura que quieres cerrar sesión?");
            alert.showAndWait();
        });

        addButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Agregar administrador");
            alert.setContentText("Aquí iría la lógica para abrir formulario de alta.");
            alert.showAndWait();
        });
    }

    private void configurarColumnas() {
        TableColumn<Usuario, Number> colId = new TableColumn<>("No.");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Usuario, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TableColumn<Usuario, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado != null && estado.equalsIgnoreCase("S") ? "Activo" : "Inactivo");
        });

        TableColumn<Usuario, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();

            {
                FontIcon editIcon = new FontIcon("fa-pencil");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");

                FontIcon viewIcon = new FontIcon("fa-eye");
                deleteButton.setGraphic(viewIcon);
                deleteButton.getStyleClass().add("action-button");

                editButton.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    onEditAdmin(usuario);
                });

                deleteButton.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    onDeleteAdmin(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        adminTable.getColumns().setAll(colId, colNombre, colUsuario, colCorreo, colEstado, colAcciones);
    }


    private void cargarAdministradores() {
        try {
            List<Usuario> admins = new UsuarioDaoImpl().findByRolNombre("ADMINISTRADOR");
            adminTable.setItems(FXCollections.observableArrayList(admins));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarAdministradores(String filtro) {
        try {
            List<Usuario> todos = new UsuarioDaoImpl().findByRolNombre("ADMINISTRADOR");
            List<Usuario> filtrados = todos.stream()
                    .filter(u -> u.getNombre().toLowerCase().contains(filtro.toLowerCase())
                            || u.getUsername().toLowerCase().contains(filtro.toLowerCase())
                            || u.getCorreo().toLowerCase().contains(filtro.toLowerCase()))
                    .toList();

            adminTable.setItems(FXCollections.observableArrayList(filtrados));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onEditAdmin(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editarAdmin.fxml"));
            Parent root = loader.load();

            //EditarAdminController controller = loader.getController();
            //controller.initData(usuario);

            Stage modalStage = new Stage();
            modalStage.setTitle("Editar administrador");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root, 600, 400));
            modalStage.showAndWait();

            cargarAdministradores(); // Refresca después de cerrar
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onDeleteAdmin(Usuario usuario) {
        // Lógica pendiente
    }
}
