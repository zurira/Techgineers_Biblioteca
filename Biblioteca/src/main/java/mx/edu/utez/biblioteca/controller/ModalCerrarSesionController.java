package mx.edu.utez.biblioteca.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModalCerrarSesionController {
    @FXML private Button aceptarButton;
    @FXML private Button cancelarButton;

    public static boolean cerrarSesionConfirmado = false;

    @FXML
    public void initialize() {
        cancelarButton.setOnAction(event -> {
            cerrarSesionConfirmado = false;
            Stage stage = (Stage) cancelarButton.getScene().getWindow();
            stage.close();
        });

        aceptarButton.setOnAction(event -> {
            cerrarSesionConfirmado = true;
            Stage stage = (Stage) aceptarButton.getScene().getWindow();
            stage.close();
        });
    }
}
