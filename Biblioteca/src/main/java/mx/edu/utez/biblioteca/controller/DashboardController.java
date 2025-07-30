package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca; // Importa tu modelo de usuario
import javafx.scene.control.Alert; // Importa Alert

import java.io.IOException;
import java.time.LocalDate; // Asegúrate de importar LocalDate para crear usuarios de prueba

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
        System.out.println("¡Botón 'Editar usuario' presionado en DashboardController!"); // <-- Depuración de clic

        // Si tu tabla de usuarios no está implementada y seleccionas un usuario REAL,
        // el usuarioSeleccionado será este de prueba.
        // Si tu tabla ya selecciona usuarios, puedes quitar este bloque de ejemplo
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Sin selección", "Por favor, selecciona un usuario para editar.");
            return;
        }

        try {
            // Carga el FXML para el modal de edición
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/EditarUsuario.fxml"));
            Parent root = loader.load();

            // Obtiene el controlador del FXML cargado
            EditarUsuarioController controller = loader.getController();

            // Pasa el usuario seleccionado al controlador del modal
            controller.cargarUsuario(usuarioSeleccionado);

            // Crea un nuevo Stage para el modal
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL); // Hace que el modal bloquee la ventana principal
            modal.setTitle("Editar usuario"); // Título del modal
            modal.setScene(new Scene(root)); // Asigna la escena al modal
            modal.showAndWait(); // Muestra el modal y espera a que se cierre

            // Si el usuario fue guardado en el modal, recarga la tabla principal (cuando la tengas)
            if (controller.isGuardado()) {
                System.out.println("Usuario guardado/actualizado. Recargando tabla...");
                recargarTablaUsuarios(); // Este método está vacío por ahora
            }

        } catch (IOException e) {
            e.printStackTrace(); // Imprime el stack trace completo si hay un error al cargar el FXML
            mostrarAlerta("Error al abrir formulario", "No se pudo abrir el formulario de edición: " + e.getMessage());
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            e.printStackTrace();
            mostrarAlerta("Error inesperado", "Ocurrió un error: " + e.getMessage());
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