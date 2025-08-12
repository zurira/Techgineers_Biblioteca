package mx.edu.utez.biblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/biblioteca/views/Prestamo.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Usuarios");
        stage.setScene(scene);
        // Esta línea maximiza la ventana.
        stage.setMaximized(true);
        stage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}
