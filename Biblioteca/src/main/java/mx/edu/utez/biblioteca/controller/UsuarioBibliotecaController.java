package mx.edu.utez.biblioteca.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import org.kordamp.ikonli.javafx.FontIcon;

private void onEditUsuario(UsuarioBiblioteca usuario) {
        System.out.println("Editar usuario: " + usuario.getNombre());
        // Implementar apertura de formulario para editar usuario
    }

    private void onDeleteUsuario(UsuarioBiblioteca usuario) {
        // No se elimina el usuario (de debe desactivar) checar eso
    }

    private void onViewUsuario(UsuarioBiblioteca usuario) {
        System.out.println("Ver detalles de usuario: " + usuario.getNombre());
        // Implementar vista de detalles del usuario
    }

    private void mostrarAlertaError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


void main() {

}