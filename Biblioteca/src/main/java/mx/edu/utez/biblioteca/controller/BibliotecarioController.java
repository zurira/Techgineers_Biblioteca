package mx.edu.utez.biblioteca.controller;

public class BibliotecarioController {
    package tu.paquete.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tu.paquete.model.Usuario;

    public class BibliotecarioController {

        @FXML private TextField txtBuscar;
        @FXML private TableView<Usuario> tablaUsuarios;

        private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

        @FXML
        public void initialize() {
            // Aquí puedes inicializar la tabla y cargar usuarios si deseas
        }

        @FXML
        public void abrirFormularioAgregar() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Agregar Usuario");
            alert.setHeaderText(null);
            alert.setContentText("Aquí abriría un modal para registrar usuario.");
            alert.showAndWait();
        }

        @FXML
        public void cerrarSesion() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cerrar sesión");
            alert.setHeaderText(null);
            alert.setContentText("Volviendo a pantalla de login...");
            alert.showAndWait();
            // Aquí puedes cambiar de escena
        }
    }
}
