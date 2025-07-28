package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.DetallePrestamoDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.EjemplarDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.net.URL;
import java.util.ResourceBundle;


public class EditarPrestamoController implements Initializable {

   @FXML private DatePicker dpFechaPrestamo;
   @FXML private DatePicker dpFechaLimite;
   @FXML private DatePicker dpFechaDevolucion;
   @FXML private ComboBox<String> cbEstado;
   @FXML private ComboBox<UsuarioBiblioteca> comboBoxUsuarios;

   private Prestamo prestamoActual;
   private final PrestamoDaoImpl prestamoDao = new PrestamoDaoImpl();
   private final EjemplarDaoImpl ejemplarDao = new EjemplarDaoImpl();
   private final DetallePrestamoDaoImpl detalleDao = new DetallePrestamoDaoImpl();

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
       // Estados
       cbEstado.setItems(FXCollections.observableArrayList("Activo", "Finalizado", "Retrasado"));
   }

      public void inicializar(Prestamo prestamo) {
       ObservableList<UsuarioBiblioteca> listaUsuarios = prestamoDao.obtenerUsuarios();
       comboBoxUsuarios.setItems(listaUsuarios);

       // Mostrar solo nombre
       comboBoxUsuarios.setCellFactory(lv -> new ListCell<>() {
           @Override
           protected void updateItem(UsuarioBiblioteca item, boolean empty) {
               super.updateItem(item, empty);
               setText(empty || item == null ? null : item.getNombre());
           }
       });
       comboBoxUsuarios.setButtonCell(new ListCell<>() {
           @Override
           protected void updateItem(UsuarioBiblioteca item, boolean empty) {
               super.updateItem(item, empty);
               setText(empty || item == null ? null : item.getNombre());
           }
       });

       // Seleccionar usuario actual del préstamo
       for (UsuarioBiblioteca u : listaUsuarios) {
           if (u.getId() == prestamo.getUsuario().getId()) {
               comboBoxUsuarios.getSelectionModel().select(u);
               break;
           }
       }
       //comboBoxUsuarios.getSelectionModel().select(prestamo.getUsuarioNombre());
       comboBoxUsuarios.setEditable(false);

       dpFechaPrestamo.setValue(prestamo.getFechaPrestamo());
       dpFechaLimite.setValue(prestamo.getFechaLimite());
       dpFechaDevolucion.setValue(prestamo.getFechaReal());

       cbEstado.setItems(FXCollections.observableArrayList("Activo", "Finalizado", "Retrasado"));
       cbEstado.setValue(prestamo.getEstado());
   }

    @FXML
   private void guardarCambiosPrestamo(ActionEvent event) {
       UsuarioBiblioteca seleccionado = comboBoxUsuarios.getSelectionModel().getSelectedItem();
       prestamoActual.setUsuario(seleccionado);
       if (dpFechaPrestamo.getValue() == null ||
               dpFechaLimite.getValue() == null ||
               cbEstado.getValue() == null) {
           mostrarAlerta("Completa todos los campos obligatorios.");
           return;
       }

       if (dpFechaLimite.getValue().isBefore(dpFechaPrestamo.getValue())) {
           mostrarAlerta("La fecha límite no puede ser anterior a la fecha de préstamo.");
           return;
       }

       if (dpFechaDevolucion.getValue() != null &&
               dpFechaDevolucion.getValue().isBefore(dpFechaPrestamo.getValue())) {
           mostrarAlerta("La fecha de devolución no puede ser anterior a la fecha de préstamo.");
           return;
       }


       // Actualizar préstamo
       prestamoActual.setFechaPrestamo(dpFechaPrestamo.getValue());
       prestamoActual.setFechaLimite(dpFechaLimite.getValue());
       prestamoActual.setFechaReal(dpFechaDevolucion.getValue());
       prestamoActual.setEstado(cbEstado.getValue());

       boolean exito = true;
       try {
           boolean actualizado = prestamoDao.update(prestamoActual);
           if (!actualizado) {
               mostrarAlerta("No se pudo actualizar el préstamo.");
               return;
           }
       } catch (Exception e) {
           e.printStackTrace(); // Puedes mostrar un error al usuario si prefieres
           mostrarAlerta("Ocurrió un error al actualizar el préstamo.");
       }



       if (exito) {
           mostrarAlerta("Cambios guardados correctamente.");
           cerrarVentana();
       } else {
           mostrarAlerta("Error al guardar los ejemplares.");
       }
   }

   @FXML
   private void cancelarAccion(ActionEvent event) {
       cerrarVentana();
   }

   private void mostrarAlerta(String mensaje) {
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle("Información");
       alert.setHeaderText(null);
       alert.setContentText(mensaje);
       alert.showAndWait();
   }

   private void cerrarVentana() {
       Stage stage = (Stage) comboBoxUsuarios.getScene().getWindow();
       stage.close();
   }


}
