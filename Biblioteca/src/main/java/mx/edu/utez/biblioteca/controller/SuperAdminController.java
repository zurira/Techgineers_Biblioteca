package mx.edu.utez.biblioteca.controller;

<<<<<<< HEAD
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
=======

>>>>>>> TTS24
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
    @FXML private Label lblSinResultados;

    private ObservableList<Usuario> listaAdmin = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarAdministradores();

<<<<<<< HEAD
        searchField.textProperty().addListener((obs, oldValue, newValue) -> filtrarAdmin(newValue));
=======
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            adminTable.getItems().setAll(
                    AdministradorDao.buscarAdministrador(newValue)
            );
        });
>>>>>>> TTS24

        adminTable.getItems().addListener((ListChangeListener<Usuario>) c -> adminTable.refresh());
        adminTable.sortPolicyProperty().set(tv -> {
            boolean sorted = TableView.DEFAULT_SORT_POLICY.call(tv);
            tv.refresh();
            return sorted;
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

<<<<<<< HEAD
        TableColumn<Usuario, Void> colAcciones = new TableColumn<>("Acciones");
=======
        TableColumn<Administrador, Void> colAcciones = new TableColumn<>("Acciones");
>>>>>>> TTS24
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();

            {
<<<<<<< HEAD
                FontIcon editIcon = new FontIcon("fa-pencil");
                editButton.setGraphic(editIcon);
                editButton.getStyleClass().add("action-button");

                editButton.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    onEditAdmin(usuario);
=======
                editButton.setOnAction(e -> {
                    Administrador admin = getTableView().getItems().get(getIndex());
                    System.out.println("Editar: " + admin.getNombreCompleto());
                    // Aquí puedes abrir el modal de edición
                });

                deleteButton.setOnAction(e -> {
                    Administrador admin = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText("Eliminar administrador");
                    confirm.setContentText("¿Estás segura que deseas eliminar a " + admin.getNombreCompleto() + "?");
                    confirm.showAndWait();
                    // Aquí puedes agregar lógica para eliminar
>>>>>>> TTS24
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });

        adminTable.getColumns().setAll(colId, colNombre, colUsuario, colCorreo, colEstado, colAcciones);
    }


    private void cargarAdministradores() {
<<<<<<< HEAD
        try {
            List<Usuario> admins = new UsuarioDaoImpl().findByRolNombre("ADMINISTRADOR");
            listaAdmin = FXCollections.observableArrayList(admins); // <-- Aquí guardas en la lista
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
=======
        adminTable.getItems().setAll(
                AdministradorDao.obtenerTodos()
        );
    }
>>>>>>> TTS24
}
