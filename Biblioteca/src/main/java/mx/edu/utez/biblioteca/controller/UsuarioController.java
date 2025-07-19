package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.biblioteca.model.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsuarioController {
    @FXML
    private TextField txtcorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private StackPane loginPane;

    @FXML
    private void onLogin(ActionEvent e){
        String correo=txtcorreo.getText().trim();
        String pass=txtPassword.getText().trim();

        UsuarioDaoImpl dao=new UsuarioDaoImpl();
        try {
            Usuario usuario = dao.login(correo,pass);
            if(usuario != null){
                System.out.println("Se pudo logear con Exito como:" + usuario.getNombreRol());

                FXMLLoader loader;
                switch (usuario.getNombreRol().trim().toUpperCase()) {
                    case "SUPERADMINISTRADOR":
                        System.out.println("Cargando vista de superadministrador");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/superadmin.fxml"));

                        break;
                    case "ADMINISTRADOR":
                        System.out.println("Cargando vista de administrador");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/admin.fxml"));
                        break;
                    case "BIBLIOTECARIO":
                        System.out.println("Cargando vista de bibliotecario");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/bibliotecario.fxml"));
                        break;
                    default:
                        showAlert("Error", "Rol no reconocido.");
                        return;
                }

                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                ((Stage) txtcorreo.getScene().getWindow()).close();
            }else{
                showAlert("Error","Credenciales incorrectas");
                System.out.println("Credenciales incorrectas!");
            }

        }catch (Exception err){
            err.printStackTrace();
            showAlert("Error", "Hubo un error en la aplicación");
        }

    }


    public void showAlert(String title, String msg){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        BackgroundImage bgImage = new BackgroundImage(
                new Image(getClass().getResource("/img/fondo.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        loginPane.setBackground(new Background(bgImage));
    }

}
