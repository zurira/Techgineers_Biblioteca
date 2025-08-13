package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.*;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class EditarPrestamoController implements Initializable {

    @FXML private DatePicker dpFechaPrestamo;
    @FXML private DatePicker dpFechaLimite;
    @FXML private DatePicker dpFechaDevolucion;
    @FXML private ComboBox<UsuarioBiblioteca> comboBoxUsuarios;

    private Prestamo prestamoActual;
    private final PrestamoDaoImpl prestamoDao = new PrestamoDaoImpl();
    private final EjemplarDaoImpl ejemplarDao = new EjemplarDaoImpl();
    private final DetallePrestamoDaoImpl detalleDao = new DetallePrestamoDaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Estados
    }

    public void inicializar(Prestamo prestamo) {
        this.prestamoActual = prestamo;
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

        //cbEstado.setItems(FXCollections.observableArrayList("Activo", "Finalizado", "Retrasado"));
        //cbEstado.setValue(prestamo.getEstado());
    }

    @FXML
    private void guardarCambiosPrestamo(ActionEvent event) {
        UsuarioBiblioteca seleccionado = comboBoxUsuarios.getSelectionModel().getSelectedItem();
        prestamoActual.setUsuario(seleccionado);

        // Validaciones de fechas
        if (dpFechaPrestamo.getValue() == null || dpFechaLimite.getValue() == null) {
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

        // Asignar fechas al préstamo actual
        prestamoActual.setFechaPrestamo(dpFechaPrestamo.getValue());
        prestamoActual.setFechaLimite(dpFechaLimite.getValue());
        prestamoActual.setFechaReal(dpFechaDevolucion.getValue());

        // 🔄 Calcular estado automáticamente
        try {
            double tarifaMulta = new ConfiguracionDaoImpl().obtenerTarifaMulta();
            String estadoCalculado = prestamoActual.calcularEstado(LocalDate.now(), tarifaMulta);
            prestamoActual.setEstado(estadoCalculado);
        } catch (SQLException e) {
            mostrarAlerta("Error al obtener la tarifa de multa.", Alert.AlertType.ERROR);
            e.printStackTrace();
            return;
        }

        // Actualizar en base de datos
        try {
            boolean actualizado = prestamoDao.update(prestamoActual);
            if (!actualizado) {
                mostrarAlerta("No se pudo actualizar el préstamo.");
                return;
            }

            mostrarAlerta("Cambios guardados correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Ocurrió un error al actualizar el préstamo.", Alert.AlertType.ERROR);
        }

        // Calcular estado según fechas
        if (prestamoActual.getFechaReal() != null) {
            prestamoActual.setEstado("Devuelto");
        } else if (LocalDate.now().isAfter(prestamoActual.getFechaLimite())) {
            prestamoActual.setEstado("Retrasado");
        } else {
            prestamoActual.setEstado("Activo");
        }
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamoActual = prestamo;

        // Establecer datos en los campos
        dpFechaPrestamo.setValue(prestamo.getFechaPrestamo());
        dpFechaLimite.setValue(prestamo.getFechaLimite());
        dpFechaDevolucion.setValue(prestamo.getFechaReal());

        try {
            // Cargar lista de usuarios y seleccionar el correspondiente
            ObservableList<UsuarioBiblioteca> usuarios = FXCollections.observableArrayList(new UsuarioBibliotecaDaoImpl().findAll());
            comboBoxUsuarios.setItems(usuarios);

            if (prestamo.getUsuario() != null) {
                for (UsuarioBiblioteca usuario : usuarios) {
                    if (usuario.getId() == prestamo.getUsuario().getId()) {
                        comboBoxUsuarios.getSelectionModel().select(usuario);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Manejo de la excepción
            e.printStackTrace();
            // Opcional: mostrar una alerta al usuario
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar los usuarios");
            alert.setContentText("No se pudo conectar con la base de datos para obtener la lista de usuarios.");
            alert.showAndWait();
        }
    }


    @FXML
    private void cancelarAccion(ActionEvent event) {
        cerrarVentana();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        mostrarAlerta(mensaje, Alert.AlertType.INFORMATION);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) comboBoxUsuarios.getScene().getWindow();
        stage.close();
    }


}