package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mx.edu.utez.biblioteca.model.Libro;

import java.sql.*;

public class LibroFormController {

    @FXML private TextField tituloField, isbnField, sinopsisField, anioField, portadaField;
    @FXML private ComboBox<String> autorCombo, categoriaCombo, editorialCombo;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private Button agregarButton;
    @FXML private ImageView portadaImageView;

    private Connection connection;

    public void initialize() {
        cargarOpcionesComboBoxes();
        estadoCombo.setItems(FXCollections.observableArrayList("ACTIVO, INACTIVO"));
    }

    private void cargarOpcionesComboBoxes() {
        cargarDatosComboBox("AUTOR", "NOMBRE", autorCombo);
        cargarDatosComboBox("CATEGORIA", "NOMBRE", categoriaCombo);
        cargarDatosComboBox("EDITORIAL", "NOMBRE", editorialCombo);
    }

    private void cargarDatosComboBox(String tabla, String columna, ComboBox<String> combo) {
        String sql = "SELECT " + columna + " FROM " + tabla;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ObservableList<String> datos = FXCollections.observableArrayList();
            while (rs.next()) {
                datos.add(rs.getString(columna));
            }
            combo.setItems(datos);
        } catch (SQLException e) {
            mostrarError("Error al cargar datos de " + tabla, e);
        }
    }

    @FXML
    private void handleAgregarLibro() {
        try {
            String titulo = tituloField.getText();
            String isbn = isbnField.getText();
            String sinopsis = sinopsisField.getText();
            int anio = Integer.parseInt(anioField.getText());
            String estado = estadoCombo.getValue();
            String autor = autorCombo.getValue();
            String categoria = categoriaCombo.getValue();
            String editorial = editorialCombo.getValue();
            String portada = portadaField.getText();

            Libro libro = new Libro(titulo, isbn, sinopsis, anio, estado, autor, categoria, editorial, portada);
            insertarLibro(libro);
            mostrarMensaje("Libro agregado exitosamente.");
            limpiarCampos();

        } catch (Exception e) {
            mostrarError("Error al agregar el libro", e);
        }
    }

    @FXML
    private void handleMostrarImagen() {
        String url = portadaField.getText();
        if (url != null && !url.isEmpty()) {
            try {
                Image imagen = new Image(url, true); // 'true' para cargar en segundo plano
                portadaImageView.setImage(imagen);
            } catch (Exception e) {
                mostrarMensaje("⚠️ No se pudo cargar la imagen. Verifica la URL.");
            }
        } else {
            mostrarMensaje("⚠️ Ingresa una URL de imagen válida.");
        }
    }

    private void insertarLibro(Libro libro) throws SQLException {
        String sql = "INSERT INTO LIBRO (TITULO, ISBN, SINOPSIS, ANIO_PUBLICACION, ESTADO, AUTOR, CATEGORIA, EDITORIAL, PORTADA) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getIsbn());
            stmt.setString(3, libro.getSinopsis());
            stmt.setInt(4, libro.getAnioPublicacion());
            stmt.setString(5, libro.getEstado());
            stmt.setString(6, libro.getAutor());
            stmt.setString(7, libro.getCategoria());
            stmt.setString(8, libro.getEditorial());
            stmt.setString(9, libro.getPortada());
            stmt.executeUpdate();
        }
    }

    private void limpiarCampos() {
        tituloField.clear();
        isbnField.clear();
        sinopsisField.clear();
        anioField.clear();
        portadaField.clear();
        autorCombo.setValue(null);
        categoriaCombo.setValue(null);
        editorialCombo.setValue(null);
        estadoCombo.setValue(null);
    }

    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
        e.printStackTrace();
    }
}