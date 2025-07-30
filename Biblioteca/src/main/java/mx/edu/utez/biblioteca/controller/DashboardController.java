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

    private UsuarioBiblioteca usuarioSeleccionado = new UsuarioBiblioteca();

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

            if (controller.isGuardado()) {
                recargarTablaUsuarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario.");
        }
    }

    private void recargarTablaUsuarios() {


    }

    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
