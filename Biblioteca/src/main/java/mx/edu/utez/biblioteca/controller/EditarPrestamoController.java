package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

}
