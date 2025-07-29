package mx.edu.utez.biblioteca.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Prestamo;

public class VerPrestamoController {

    @FXML
    private TextField txtNombreUsuario;
    @FXML
    private TextField txtTituloLibro;
    @FXML
    private TextField txtFechaPrestamo;
    @FXML
    private TextField txtFechaLimite;
    @FXML
    private TextField txtFechaReal;
    @FXML
    private TextField txtEstado;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPrestamo(Prestamo prestamo) {
        if (prestamo != null) {
            txtNombreUsuario.setText(prestamo.getUsuario() != null ? prestamo.getUsuario().getNombre() : "N/A");
            txtTituloLibro.setText(prestamo.getLibro() != null ? prestamo.getLibro().getTitulo() : "N/A");
            txtFechaPrestamo.setText(prestamo.getFechaPrestamo() != null ? prestamo.getFechaPrestamo().toString() : "");
            txtFechaLimite.setText(prestamo.getFechaLimite() != null ? prestamo.getFechaLimite().toString() : "");
            txtFechaReal.setText(prestamo.getFechaReal() != null ? prestamo.getFechaReal().toString() : "");
            txtEstado.setText(prestamo.getEstado());

            txtNombreUsuario.setEditable(false);
            txtTituloLibro.setEditable(false);
            txtFechaPrestamo.setEditable(false);
            txtFechaLimite.setEditable(false);
            txtFechaReal.setEditable(false);
            txtEstado.setEditable(false);
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleGuardar() {
        System.out.println("No se debería llamar a guardar en una vista de solo lectura.");
        dialogStage.close();
    }


}
