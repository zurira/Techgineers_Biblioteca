package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // --- SECCIÓN DE DEPURACIÓN DE RUTAS ---
        URL fxmlUrl = getClass().getResource("/mx/edu/utez/biblioteca/views/Dashboard.fxml");
        if (fxmlUrl == null) {
            System.err.println("¡ERROR FATAL: Dashboard.fxml NO se encontró en el classpath!");
            throw new RuntimeException("FXML no encontrado: /mx/edu/utez/biblioteca/views/Dashboard.fxml");
        } else {
            System.out.println("Dashboard.fxml ENCONTRADO en: " + fxmlUrl);
        }
        // --- FIN DE SECCIÓN DE DEPURACIÓN ---

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // *** Cargar y aplicar el CSS programáticamente ***
        URL cssUrl = getClass().getResource("/mx/edu/utez/biblioteca/css/Dashboard.css");
        if (cssUrl == null) {
            System.err.println("¡ADVERTENCIA: Dashboard.css NO se encontró en el classpath para carga programática!");
        } else {
            System.out.println("Dashboard.css ENCONTRADO programáticamente en: " + cssUrl);
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        // ************************************************

        stage.setTitle("Mi Aplicación Biblioteca");
        stage.setScene(scene);
        stage.show();

        System.out.println("¡Aplicación JavaFX debería estar visible!");
    }


    public static void main(String[] args) {
        launch();
    }
}