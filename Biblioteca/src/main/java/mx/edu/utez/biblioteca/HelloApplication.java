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
        // Ajusta la ruta al archivo FXML, por ejemplo "Usuarios.fxml"
        String fxmlPath = "mx/edu/utez/biblioteca/views/AgregarUsuario.fxml";

        URL fxmlUrl = getClass().getClassLoader().getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new RuntimeException("No se encontró el archivo FXML en recursos: " + fxmlPath);
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