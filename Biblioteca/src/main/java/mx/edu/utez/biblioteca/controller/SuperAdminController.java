package mx.edu.utez.biblioteca.controller;




import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class SuperAdminController {

    @FXML private TableView<?> adminTable;
    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        // TODO: Configurar columnas, carga de datos reales

        // 👉 Aquí colocarás la lógica de cerrar sesión con ventana emergente
        logoutButton.setOnAction(event -> {
            // TODO: Mostrar ventana de confirmación de cerrar sesión
        });

        addButton.setOnAction(event -> {
            // TODO: Abrir ventana para agregar nuevo administrador
        });

        // Para cada fila podrás usar una celda personalizada para "Acciones"
        // con botones de editar y eliminar
    }
}