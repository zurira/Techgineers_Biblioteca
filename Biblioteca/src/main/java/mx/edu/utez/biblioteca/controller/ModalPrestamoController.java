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
            mostrarAlerta("Por favor, selecciona un usuario");
            return;
        }

        if (dpFechaPrestamo.getValue() == null || dpFechaLimite.getValue() == null || cbEstado.getValue() == null) {
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

        List<Ejemplar> seleccionados = tablaEjemplares.getItems().stream()
                .filter(Ejemplar::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Debes seleccionar al menos un ejemplar.");
            return;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuarioSeleccionado);
        prestamo.setFechaPrestamo(dpFechaPrestamo.getValue());
        prestamo.setFechaLimite(dpFechaLimite.getValue());
        prestamo.setFechaReal(dpFechaDevolucion.getValue());
        prestamo.setEstado(cbEstado.getValue());

        try {
            boolean creado = prestamoDAO.create(prestamo);

            if (!creado) {
                mostrarAlerta("No se pudo crear el préstamo.");
                return;
            }

            int idPrestamo = prestamo.getId();
            boolean exito = true;

            for (Ejemplar ej : seleccionados) {
                boolean detalleInsertado = detalleDAO.insertarEjemplar(idPrestamo, ej.getIdEjemplar());
                boolean disponibilidadActualizada = ejemplarDAO.actualizarDisponibilidad(ej.getIdEjemplar(), false);

                if (!detalleInsertado || !disponibilidadActualizada) {
                    exito = false;
                    break;
                }
            }

            if (exito) {
                mostrarAlerta("Préstamo registrado exitosamente con todos los ejemplares.");
                cerrarVentana();
            } else {
                mostrarAlerta("Ocurrió un error al registrar uno o más ejemplares.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Ocurrió un error inesperado al registrar el préstamo.");
        }
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
