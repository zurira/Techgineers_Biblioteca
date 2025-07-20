package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/biblioteca/views/bienvenida.fxml"));
        Scene scene = new Scene(root, 1024, 640);
        stage.setTitle("Biblioteca");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}