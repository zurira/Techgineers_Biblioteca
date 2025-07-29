package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL fxmlPath = getClass().getResource("/mx/edu/utez/biblioteca/views/AgregarUsuario.fxml");
        if (fxmlPath == null) {
            System.out.println("¡No se encontró el archivo FXML!");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlPath);
        Scene scene = new Scene(fxmlLoader.load());

        // Agregar CSS desde Java (opcional si no está en el FXML)
        URL cssPath = getClass().getResource("/mx/edu/utez/biblioteca/css/AgregarUsuario.css");
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath.toExternalForm());
        }

        stage.setTitle("Biblioteca - Agregar Usuario");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}