package mx.edu.utez.biblioteca.controller;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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


import java.net.URL;
import java.util.ResourceBundle;

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

    // Aquí voy a agregar los  métodos para guardar o cancelar
}
