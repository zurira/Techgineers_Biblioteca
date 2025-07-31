package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL; // Asegúrate de importar URL
import java.time.LocalDate;

public class DashboardController {

    // IMPORTANTÍSIMO: Por ahora, este usuario está vacío. Esto es para que NO falle el modal.
    // En una aplicación real, este usuario vendría de una selección de la tabla.
    private UsuarioBiblioteca usuarioSeleccionado = new UsuarioBiblioteca(
            1, // id de ejemplo
            "Usuario de Prueba", // nombre de ejemplo
            LocalDate.of(1990, 5, 15), // fecha de nacimiento de ejemplo
            "prueba@example.com", // correo de ejemplo
            "5512345678", // teléfono de ejemplo
            "Calle Falsa 123", // dirección de ejemplo
            "S" // estado de ejemplo (Activo)
    );

    @FXML
    private void abrirModalEdicion(ActionEvent event) {
        System.out.println("¡Botón 'Editar usuario' presionado en DashboardController!");

        if (usuarioSeleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor, selecciona un usuario para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarUsuario.fxml")); // Usamos AgregarUsuario.fxml para edición
            Parent root = loader.load();

            // El controlador es AgregarUsuarioController, no EditarUsuarioController
            AgregarUsuarioController controller = loader.getController();
            controller.setUsuarioParaFormulario(usuarioSeleccionado); // Método para cargar datos en el formulario

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar usuario");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isGuardado()) {
                System.out.println("Usuario guardado/actualizado. Recargando tabla...");
                recargarTablaUsuarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir formulario", "No se pudo abrir el formulario de edición: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage());
        }
    }

    @FXML
    private void abrirModalAgregarUsuario(ActionEvent event) { // <-- NUEVO MÉTODO PARA AGREGAR
        System.out.println("¡Botón 'Agregar usuario' presionado en DashboardController!");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarUsuario.fxml"));
            Parent root = loader.load();

            AgregarUsuarioController controller = loader.getController();
            controller.setUsuarioParaFormulario(null); // Pasa null para indicar que es un nuevo usuario

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Agregar nuevo usuario");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isGuardado()) {
                System.out.println("Nuevo usuario agregado. Recargando tabla...");
                recargarTablaUsuarios();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir formulario", "No se pudo abrir el formulario de agregar usuario: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage());
        }
    }


    @FXML
    private void abrirModalVerUsuario(ActionEvent event) { // <-- MÉTODO PARA VER USUARIO
        System.out.println("¡Botón 'Ver detalles' presionado en DashboardController!");

        if (usuarioSeleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor, selecciona un usuario para ver sus detalles.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/VerUsuario.fxml"));
            Parent root = loader.load();

            VerUsuarioController controller = loader.getController();
            controller.cargarUsuario(usuarioSeleccionado); // Pasa el usuario seleccionado

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Detalles del Usuario");
            modal.setScene(new Scene(root));
            modal.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir detalles", "No se pudo abrir la ventana de detalles: " + e.getMessage());
        }
    }


    private void recargarTablaUsuarios() {
        // Aquí iría la lógica para volver a cargar los datos de la tabla de usuarios
        // desde la base de datos y actualizar la TableView en el Dashboard.
        System.out.println("Método recargarTablaUsuarios llamado (lógica por implementar).");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}