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
import mx.edu.utez.biblioteca.dao.impl.DetallePrestamoDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.EjemplarDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.PrestamoDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.biblioteca.model.Ejemplar;
import mx.edu.utez.biblioteca.model.Prestamo;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModalPrestamoController implements Initializable {

    @FXML private ComboBox<String> comboBoxUsuarios;
    @FXML private TextField txtBuscarEjemplar;
    @FXML private DatePicker dpFechaPrestamo, dpFechaLimite, dpFechaDevolucion;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Ejemplar> tablaEjemplares;
    @FXML private TableColumn<Ejemplar, String> colCodigo, colTitulo, colUbicacion;
    @FXML private TableColumn<Ejemplar, Boolean> colSeleccionar;

    private final UsuarioDaoImpl usuarioDAO = new UsuarioDaoImpl();
    private final EjemplarDaoImpl ejemplarDAO = new EjemplarDaoImpl();
    private final PrestamoDaoImpl prestamoDAO = new PrestamoDaoImpl();
    private final DetallePrestamoDaoImpl detalleDAO = new DetallePrestamoDaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Estados disponibles
        cbEstado.setItems(FXCollections.observableArrayList("Activo", "Finalizado", "Retrasado"));
        cbEstado.getSelectionModel().selectFirst();

        // Configurar columnas de la tabla de ejemplares
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colSeleccionar.setCellValueFactory(cellData -> cellData.getValue().seleccionadoProperty());
        colSeleccionar.setCellFactory(CheckBoxTableCell.forTableColumn(colSeleccionar));
        tablaEjemplares.setEditable(true);

        // Carga de todos los ejemplares disponibles
        ObservableList<Ejemplar> ejemplaresTotales = ejemplarDAO.buscarEjemplaresDisponibles("");
        FilteredList<Ejemplar> ejemplaresFiltrados = new FilteredList<>(ejemplaresTotales, e -> true);
        tablaEjemplares.setItems(ejemplaresFiltrados);

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
        String nombreUsuario = comboBoxUsuarios.getEditor().getText().trim();

        if (nombreUsuario.isEmpty()
                || dpFechaPrestamo.getValue() == null
                || dpFechaLimite.getValue() == null
                || cbEstado.getValue() == null) {
            mostrarAlerta("Completa todos los campos obligatorios.");
            return;
        }

        // Validar fechas
        if (dpFechaLimite.getValue().isBefore(dpFechaPrestamo.getValue())) {
            mostrarAlerta("La fecha límite no puede ser anterior a la fecha de préstamo.");
            return;
        }

        if (dpFechaDevolucion.getValue() != null &&
                dpFechaDevolucion.getValue().isBefore(dpFechaPrestamo.getValue())) {
            mostrarAlerta("La fecha de devolución no puede ser anterior a la fecha de préstamo.");
            return;
        }

        // Valida si el usuario existe, mediante el ID
        int idUsuario = usuarioDAO.obtenerIdPorNombre(nombreUsuario);
        if (idUsuario == -1) {
            mostrarAlerta("El usuario seleccionado no existe.");
            return;
        }

        List<Ejemplar> seleccionados = tablaEjemplares.getItems().stream()
                .filter(Ejemplar::isSeleccionado)
                .collect(Collectors.toList());

        // Valida que haya un ejemplar seleccionado
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Debes seleccionar al menos un ejemplar.");
            return;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setIdUsuario(idUsuario); // usuario solicitante
        prestamo.setFechaPrestamo(dpFechaPrestamo.getValue());
        prestamo.setFechaLimite(dpFechaLimite.getValue());
        prestamo.setFechaDevolucion(dpFechaDevolucion.getValue());
        prestamo.setEstado(cbEstado.getValue());

        int idPrestamo = prestamoDAO.insertar(prestamo);

        //valida si el prestamo tuvo errores
        if (idPrestamo == -1) {
            mostrarAlerta("Error al registrar el préstamo.");
            return;
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Aquí voy a agregar los  métodos para guardar o cancelar
}
