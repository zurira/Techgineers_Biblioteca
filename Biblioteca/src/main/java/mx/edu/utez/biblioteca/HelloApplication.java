package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlPath = getClass().getResource("/mx/edu/utez/biblioteca/views/Dashboard.fxml");
        if (fxmlPath == null) {
            System.out.println("❌ No se encontró Dashboard.fxml");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlPath);
        Scene scene = new Scene(fxmlLoader.load());

        // Si tienes un CSS específico para el dashboard
        URL cssPath = getClass().getResource("/mx/edu/utez/biblioteca/css/dashboard.css");
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath.toExternalForm());
        }

        stage.setTitle("Biblioteca - Panel Principal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}