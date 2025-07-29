package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlPath = "mx/edu/utez/biblioteca/views/AgregarUsuario.fxml";

        URL fxmlUrl = getClass().getClassLoader().getResource("mx/edu/utez/biblioteca/views/AgregarUsuario.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("No se encontró el archivo FXML en recursos: " + "mx/edu/utez/biblioteca/views/AgregarUsuario.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Bibliotecario – Agregar Usuario");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}