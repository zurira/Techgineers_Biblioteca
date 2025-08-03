package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override

    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/bienvenida.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Prestamos de libros");
        Scene scene = new Scene(root, 1024, 640);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}