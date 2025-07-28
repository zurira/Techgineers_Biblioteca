package mx.edu.utez.biblioteca.controller;



import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mx.edu.utez.biblioteca.dao.impl.AdministradorDao;
import mx.edu.utez.biblioteca.model.Administrador;

public class SuperAdminController {
    @FXML private TableView<Administrador> adminTable;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarAdministradores();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            AdministradorDao.buscarAdministrador(adminTable, newValue);
        });

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
        TableColumn<Administrador, Number> colId = new TableColumn<>("No.");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Administrador, String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));

        TableColumn<Administrador, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<Administrador, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TableColumn<Administrador, Boolean> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Estas son las  Columnas de acciones
        TableColumn<Administrador, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox hbox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    Administrador admin = getTableView().getItems().get(getIndex());
                    System.out.println("Editar: " + admin.getNombreCompleto());
                    // Aquie va a ir la Lógica para abrir ventana de edición
                });

                deleteButton.setOnAction(e -> {
                    Administrador admin = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText("Eliminar administrador");
                    confirm.setContentText("¿Estás segura que deseas eliminar a " + admin.getNombreCompleto() + "?");
                    confirm.showAndWait();
                    // Aquí iría la lógica DAO para eliminar
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        adminTable.getColumns().setAll(colId, colNombre, colUsuario, colCorreo, colEstado, colAcciones);
    }

    private void cargarAdministradores() {
        AdministradorDao.cargarAdministradores(adminTable);
    }
}