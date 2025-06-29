package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {//se estructuraron las carpetas
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 700, 450);
        stage.setTitle("Login Biblioteca");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}