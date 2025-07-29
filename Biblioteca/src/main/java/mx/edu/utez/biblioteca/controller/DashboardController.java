package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.controller.EditarUsuarioController;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.io.IOException;

public class DashboardController {

    // Este sería el usuario que seleccionaste en tu TableView, lo puedes obtener con getSelectionModel().getSelectedItem()
    private UsuarioBiblioteca usuarioSeleccionado;

    @FXML
    private void abrirModalEdicion(ActionEvent event) {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Sin selección", "Selecciona un usuario para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/EditarUsuario.fxml"));
            Parent root = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.cargarUsuario(usuarioSeleccionado); // Inyectamos el usuario a editar

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar usuario");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            // Si se guardó correctamente, recargamos los datos
            if (controller.isGuardado()) {
                recargarTablaUsuarios(); // Método que tú defines para refrescar tu vista
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario.");
        }
    }

    // Método auxiliar para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Este método deberías implementarlo tú para que actualice tu tabla:
    private void recargarTablaUsuarios() {
        // Ejemplo:
        // tablaUsuarios.setItems(FXCollections.observableArrayList(dao.findAll()));
        // tablaUsuarios.refresh();
    }
}