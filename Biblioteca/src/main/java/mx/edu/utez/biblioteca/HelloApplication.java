package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // Asegúrate de importar URL

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Depuración de FXML: Asegurarnos de que el FXML se encuentra
        URL fxmlUrl = getClass().getResource("/mx/edu/utez/biblioteca/views/Dashboard.fxml");
        if (fxmlUrl == null) {
            System.err.println("¡ERROR: Dashboard.fxml NO se encontró en el classpath!");
            throw new RuntimeException("FXML no encontrado: /mx/edu/utez/biblioteca/views/Dashboard.fxml");
        } else {
            System.out.println("Dashboard.fxml ENCONTRADO en: " + fxmlUrl);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl); // Usa la URL verificada
        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Dale un tamaño inicial decente

        // *** Cargar y aplicar el CSS programáticamente ***
        URL cssUrl = getClass().getResource("/mx/edu/utez/biblioteca/css/Dashboard.css");
        if (cssUrl == null) {
            System.err.println("¡ERROR: Dashboard.css NO se encontró en el classpath para carga programática!");
            // No lanzamos una excepción aquí para ver si la ventana se abre sin el CSS
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