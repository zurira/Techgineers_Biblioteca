package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Necesario para getClass().getResource()

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // --- INICIO DE SECCIÓN DE DEPURACIÓN DE RUTAS ---
        // Verificar que el FXML se encuentra
        URL fxmlUrl = getClass().getResource("/mx/edu/utez/biblioteca/views/Dashboard.fxml");
        if (fxmlUrl == null) {
            System.err.println("¡ERROR FATAL: Dashboard.fxml NO se encontró en el classpath!");
            throw new RuntimeException("FXML no encontrado: /mx/edu/utez/biblioteca/views/Dashboard.fxml");
        } else {
            System.out.println("Dashboard.fxml ENCONTRADO en: " + fxmlUrl);
        }

        // Verificar que el CSS se encuentra (opcional si lo pones en FXML, pero bueno para depurar)
        URL cssUrl = getClass().getResource("/mx/edu/utez/biblioteca/css/Dashboard.css");
        if (cssUrl == null) {
            System.err.println("¡ADVERTENCIA: Dashboard.css NO se encontró en el classpath!");
            // No lanzar excepción aquí, para que la app se abra sin estilos si el CSS falla
        } else {
            System.out.println("Dashboard.css ENCONTRADO en: " + cssUrl);
        }
        // --- FIN DE SECCIÓN DE DEPURACIÓN ---

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl); // Usamos la URL verificada
        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Tamaño inicial, ajusta según necesites

        // Si el CSS no se carga directamente en el FXML (usando stylesheets="@..."),
        // puedes cargarlo programáticamente aquí.
        // Si YA lo tienes en el FXML, este bloque es redundante.
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }


        stage.setTitle("Mi Aplicación Biblioteca"); // Título de la ventana
        stage.setScene(scene); // Establece la escena
        stage.show(); // Muestra la ventana

        System.out.println("¡Aplicación JavaFX se ha inicializado y debería estar visible!");
    }

    public static void main(String[] args) {
        launch(); // Método para lanzar la aplicación JavaFX
    }
}