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
    //Metodo funcionando, ya realiza la validación de cada rol
    @FXML
    private void onLogin(ActionEvent e){
        String input=txtcorreo.getText().trim();
        String pass=txtPassword.getText().trim();

        if (input.isEmpty() || pass.isEmpty()) {
            showAlert("Advertencia", "Correo/Usuario y contraseña son requeridos.");
            return;
        }

        UsuarioDaoImpl dao=new UsuarioDaoImpl();
        try {
            Usuario usuario = dao.login(input,pass);
            if(usuario != null){
                System.out.println("Se pudo logear con Exito como:" + usuario.getRol().getNombre());

                FXMLLoader loader;
                switch (usuario.getRol().getNombre().trim().toUpperCase()) {
                    case "SUPERADMINISTRADOR":
                        System.out.println("Cargando vista de superadministrador");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/superadmin-view.fxml"));

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

                //carga la vista y cierra la anterior
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

    //inicializa la vista del login con imagen de fondo
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

    @FXML
    private void irInicio(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/bienvenida.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 720);
            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}