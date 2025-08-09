package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class SuperAdminController {
    @FXML
    private TableView<Usuario> adminTable;
    @FXML
    private TextField searchField;
    @FXML
    private Button addButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label lblSinResultados;

    private ObservableList<Usuario> listaAdmin = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();

        //  Distribución proporcional de columnas
        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        adminTable.getColumns().get(0).setMaxWidth(1f * Integer.MAX_VALUE * 5);   // No.
        adminTable.getColumns().get(1).setMaxWidth(1f * Integer.MAX_VALUE * 20);  // Nombre completo
        adminTable.getColumns().get(2).setMaxWidth(1f * Integer.MAX_VALUE * 15);  // Usuario
        adminTable.getColumns().get(3).setMaxWidth(1f * Integer.MAX_VALUE * 25);  // Correo
        adminTable.getColumns().get(4).setMaxWidth(1f * Integer.MAX_VALUE * 10);  // Estado
        adminTable.getColumns().get(5).setMaxWidth(1f * Integer.MAX_VALUE * 25);  // Acciones

        cargarAdministradores();

        searchField.textProperty().addListener((obs, oldValue, newValue) -> filtrarAdmin(newValue));

        adminTable.getItems().addListener((ListChangeListener<Usuario>) c -> adminTable.refresh());
        adminTable.sortPolicyProperty().set(tv -> {
            boolean sorted = TableView.DEFAULT_SORT_POLICY.call(tv);
            tv.refresh();
            return sorted;
        });
        addButton.setOnAction(event -> onAddAdmin());
        logoutButton.setOnAction(event -> cerrarSesion());
    }

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
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
                Parent loginRoot = loginLoader.load();

                Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                currentStage.setScene(new Scene(loginRoot, 600, 400));
                currentStage.setTitle("Inicio de sesión");
            }

            ModalCerrarSesionController.cerrarSesionConfirmado = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarColumnas() {
        // Columna de índice
        TableColumn<Usuario, Void> colId = new TableColumn<>("No.");
        colId.setCellFactory(column -> new TableCell<Usuario, Void>() {
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

        TableColumn<Usuario, String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Usuario, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TableColumn<Usuario, String> colEstado = new TableColumn<>("Estado");

// 1. Agregue esto convertir "S"/"N" a "Activo"/"Inactivo"
        colEstado.setCellValueFactory(cellData -> {
            String estado = cellData.getValue().getEstado(); // ← Aquí obtienes "S" o "N"
            String textoEstado = estado.equalsIgnoreCase("S") ? "Activo" : "Inactivo";
            return new SimpleStringProperty(textoEstado);
        });

// 2. Aplique estilos visuales
        colEstado.setCellFactory(column -> new TableCell<Usuario, String>() {
            private final Label estadoLabel = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    estadoLabel.setText(item);
                    estadoLabel.getStyleClass().setAll("status-label");

                    if (item.equalsIgnoreCase("Activo")) {
                        estadoLabel.getStyleClass().add("status-active");
                    } else {
                        estadoLabel.getStyleClass().add("status-inactive");
                    }

                    setGraphic(estadoLabel);
                    setAlignment(Pos.CENTER);
                }
            }
        });



        TableColumn<Usuario, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button viewButton = new Button();
            private final Button switchButton = new Button();

            {
                editButton.getStyleClass().add("action-button");
                viewButton.getStyleClass().add("action-button");
                switchButton.getStyleClass().add("action-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());

                    FontIcon editIcon = new FontIcon("fa-pencil");
                    editIcon.getStyleClass().add("action-icon");
                    editButton.setGraphic(editIcon);

                    FontIcon viewIcon = new FontIcon("fa-eye");
                    viewIcon.getStyleClass().add("action-icon");
                    viewButton.getStyleClass().setAll("action-button", "eye-button");
                    viewButton.setGraphic(viewIcon);

                    FontIcon switchIcon;
                    if ("S".equalsIgnoreCase(usuario.getEstado())) {
                        switchIcon = new FontIcon("fa-toggle-on");
                        switchButton.setTooltip(new Tooltip("Desactivar"));
                    } else {
                        switchIcon = new FontIcon("fa-toggle-off");
                        switchButton.setTooltip(new Tooltip("Activar"));
                    }
                    switchIcon.getStyleClass().add("action-icon");
                    switchButton.setGraphic(switchIcon);

                    editButton.setOnAction(event -> onEditAdmin(usuario));
                    viewButton.setOnAction(event -> onViewAdmin(usuario));
                    switchButton.setOnAction(event -> onToggleEstado(usuario));

                    HBox buttons = new HBox(5, editButton, viewButton, switchButton);
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
            listaAdmin = FXCollections.observableArrayList(admins);
            adminTable.setItems(listaAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filtrarAdmin(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            adminTable.setItems(listaAdmin);
            lblSinResultados.setVisible(false);
            return;
        }

        String filtroLower = filtro.toLowerCase();
        ObservableList<Usuario> adminsFiltrados = FXCollections.observableArrayList();

        for (Usuario p : listaAdmin) {
            String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
            String usuario = p.getUsername() != null ? p.getUsername().toLowerCase() : "";
            String correo = p.getCorreo() != null ? p.getCorreo().toLowerCase() : "";
            String estado = p.getEstado() != null ? p.getEstado().toLowerCase() : "";

            if (nombre.contains(filtroLower) || usuario.contains(filtroLower)
                    || correo.contains(filtroLower) || estado.contains(filtroLower)) {
                adminsFiltrados.add(p);
            }
        }

        adminTable.setItems(adminsFiltrados);
        lblSinResultados.setVisible(adminsFiltrados.isEmpty());
    }

    private void onEditAdmin(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/editAdmin.fxml"));
            Parent root = loader.load();

            EditAdminController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage modalStage = new Stage();
            modalStage.setTitle("Editar administrador");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            cargarAdministradores();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onAddAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/ModalAgregarAdministrador.fxml"));
            Parent root = loader.load();

            ModalAgregarAdminController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.setTitle("Agregar administrador");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

            if (controller.seAgregoUsuario()) {
                cargarAdministradores();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDeleteAdmin(Usuario usuario) {
        // Eliminación desactivada por decisión de Tania
    }

    private void onViewAdmin(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/ModalVerAdmin.fxml"));
            Parent root = loader.load();

            ModalVerAdminController controller = loader.getController();
            controller.setUsuario(usuario);

            Stage modalStage = new Stage();
            modalStage.setTitle("Detalles del administrador");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Modifique eso
    private void onToggleEstado(Usuario usuario) {
        try {
            String nuevoEstado = "S".equalsIgnoreCase(usuario.getEstado()) ? "N" : "S";
            usuario.setEstado(nuevoEstado);
            new UsuarioDaoImpl().update(usuario);
            cargarAdministradores();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cambiar el estado del administrador.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}