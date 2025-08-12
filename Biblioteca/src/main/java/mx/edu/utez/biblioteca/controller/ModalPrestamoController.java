package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import mx.edu.utez.biblioteca.dao.impl.DetallePrestamoDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.EjemplarDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.UsuarioBibliotecaDaoImpl;
import mx.edu.utez.biblioteca.model.Ejemplar;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModalPrestamoController implements Initializable {

    @FXML private ComboBox<UsuarioBiblioteca> comboBoxUsuarios;
    @FXML private TextField txtBuscarEjemplar;
    @FXML private DatePicker dpFechaPrestamo, dpFechaLimite, dpFechaDevolucion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Ejemplar> tablaEjemplares;
    @FXML private TableColumn<Ejemplar, String> colCodigo, colTitulo, colUbicacion;
    @FXML private TableColumn<Ejemplar, Boolean> colSeleccionar;

    private final UsuarioBibliotecaDaoImpl usuarioDAO = new UsuarioBibliotecaDaoImpl();
    private final EjemplarDaoImpl ejemplarDAO = new EjemplarDaoImpl();
    private final PrestamoDaoImpl prestamoDAO = new PrestamoDaoImpl();
    private final DetallePrestamoDaoImpl detalleDAO = new DetallePrestamoDaoImpl();

    private ObservableList<UsuarioBiblioteca> listaUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbEstado.setItems(FXCollections.observableArrayList("Activo", "Finalizado", "Retrasado"));
        cbEstado.getSelectionModel().selectFirst();

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colSeleccionar.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
        colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccionar));
        tablaEjemplares.setEditable(true);

        ObservableList<Ejemplar> ejemplaresTotales = ejemplarDAO.buscarEjemplaresDisponibles("");
        tablaEjemplares.setItems(new FilteredList<>(ejemplaresTotales, e -> true));

        listaUsuarios = FXCollections.observableArrayList(prestamoDAO.obtenerUsuarios());
        comboBoxUsuarios.setItems(listaUsuarios);

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

        // Filtro interactivo
        comboBoxUsuarios.setEditable(true);
        comboBoxUsuarios.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            FilteredList<UsuarioBiblioteca> filtrados = listaUsuarios.filtered(usuario ->
                    usuario.getNombre().toLowerCase().contains(newVal.toLowerCase()));
            comboBoxUsuarios.setItems(filtrados);
            comboBoxUsuarios.show();
        });

        comboBoxUsuarios.setConverter(new StringConverter<UsuarioBiblioteca>() {
            @Override
            public String toString(UsuarioBiblioteca user) {
                return user != null ? user.getNombre() : "";
            }

            @Override
            public UsuarioBiblioteca fromString(String nombre) {
                // Busca el objeto completo en tu lista según el nombre
                for (UsuarioBiblioteca usuario : listaUsuarios) {
                    if (usuario.getNombre().equalsIgnoreCase(nombre)) {
                        return usuario;
                    }
                }
                return null;
            }
        });

    }

    @FXML
    private void buscarEjemplares(ActionEvent event) {
        try {
            String filtro = txtBuscarEjemplar.getText().trim();
            ObservableList<Ejemplar> resultados = ejemplarDAO.buscarEjemplaresDisponibles(filtro);
            tablaEjemplares.setItems(resultados);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error interno al buscar ejemplares: " + e.getMessage());
        }
    }

    @FXML
    private void registrarPrestamo(ActionEvent event) {
        UsuarioBiblioteca usuarioSeleccionado = comboBoxUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            mostrarAlerta("Por favor, selecciona un usuario.");
            return;
        }

        if (dpFechaPrestamo.getValue() == null || dpFechaLimite.getValue() == null || cbEstado.getValue() == null) {
            mostrarAlerta("Completa todos los campos obligatorios.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaPrestamo = dpFechaPrestamo.getValue();
        LocalDate fechaLimite = dpFechaLimite.getValue();
        LocalDate fechaDevolucion = dpFechaDevolucion.getValue();

        // Validaciones contra el día actual
        if (fechaPrestamo.isBefore(hoy) || fechaPrestamo.isAfter(hoy)) {
            mostrarAlerta("La fecha de préstamo debe ser el día actual.");
            return;
        }

        if (fechaLimite.isBefore(hoy)) {
            mostrarAlerta("La fecha límite no puede ser anterior al día actual.");
            return;
        }

        if (fechaLimite.isBefore(fechaPrestamo)) {
            mostrarAlerta("La fecha límite no puede ser anterior a la fecha de préstamo.");
            return;
        }

        if (fechaDevolucion != null) {
            if (fechaDevolucion.isBefore(hoy)) {
                mostrarAlerta("La fecha de devolución no puede ser anterior al día actual.");
                return;
            }

            if (fechaDevolucion.isBefore(fechaPrestamo)) {
                mostrarAlerta("La fecha de devolución no puede ser anterior a la fecha de préstamo.");
                return;
            }
        }

        List<Ejemplar> seleccionados = tablaEjemplares.getItems().stream()
                .filter(Ejemplar::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Debes seleccionar al menos un ejemplar.");
            return;
        }

        // Se crea una lista para almacenar los IDs de los préstamos insertados
        List<Integer> prestamoIds = new ArrayList<>();

        try {
            boolean exito = true;

            // Iteramos sobre cada ejemplar seleccionado para crear un préstamo individual
            for (Ejemplar ejemplarSeleccionado : seleccionados) {
                Prestamo prestamo = new Prestamo();
                prestamo.setUsuario(usuarioSeleccionado);
                prestamo.setFechaPrestamo(fechaPrestamo);
                prestamo.setFechaLimite(fechaLimite);
                prestamo.setFechaReal(fechaDevolucion);
                prestamo.setEstado(cbEstado.getValue());
                prestamo.setEjemplar(ejemplarSeleccionado); // Asignar el objeto Ejemplar al préstamo

                prestamo.setIdEjemplar(ejemplarSeleccionado.getIdEjemplar());

                // Insertamos el préstamo en la base de datos
                boolean creado = prestamoDAO.create(prestamo);

                if (creado) {
                    // Si el préstamo se crea, guardamos su ID
                    prestamoIds.add(prestamo.getId());

                    // Actualizamos el estado del ejemplar a 'PRESTADO'
                    boolean disponibilidadActualizada = ejemplarDAO.actualizarDisponibilidad(ejemplarSeleccionado.getIdEjemplar(), false);

                    if (!disponibilidadActualizada) {
                        exito = false;
                        System.err.println("Error al actualizar la disponibilidad del ejemplar: " + ejemplarSeleccionado.getIdEjemplar());
                        break;
                    }

                } else {
                    exito = false;
                    System.err.println("Error al crear el préstamo para el ejemplar: " + ejemplarSeleccionado.getIdEjemplar());
                    break;
                }
            }

            if (exito) {
                mostrarAlerta("Préstamo(s) registrado(s) exitosamente.");
                cerrarVentana();
            } else {
                // Aquí puedes agregar lógica para hacer rollback si es necesario
                mostrarAlerta("Ocurrió un error al registrar uno o más préstamos. Revise el log.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Ocurrió un error inesperado al registrar el préstamo.");
        }

        limpiarFormulario();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) comboBoxUsuarios.getScene().getWindow();
        stage.close();
    }

    private void limpiarFormulario() {
        comboBoxUsuarios.getEditor().clear();
        txtBuscarEjemplar.clear();
        tablaEjemplares.getItems().clear();
        dpFechaPrestamo.setValue(null);
        dpFechaLimite.setValue(null);
        cbEstado.getSelectionModel().selectFirst();
        comboBoxUsuarios.setItems(listaUsuarios);
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void cancelarAccion(ActionEvent event) {
        cerrarVentana();
    }
}