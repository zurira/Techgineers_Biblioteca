package mx.edu.utez.biblioteca.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainModalCerrarSesion extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/biblioteca/views/ModalCerrarSesion.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Cerrar sesión");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

