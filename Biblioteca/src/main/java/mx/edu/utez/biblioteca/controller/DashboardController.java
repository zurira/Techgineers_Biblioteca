package mx.edu.utez.biblioteca.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

        @FXML
        private void abrirModalEdicion(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/EditarUsuario.fxml"));
                Parent root = loader.load();

                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Editar usuario");
                modal.setScene(new Scene(root));
                modal.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }