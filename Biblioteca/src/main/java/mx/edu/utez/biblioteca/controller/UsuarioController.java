package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;

public class UsuarioController {
    @FXML
    private TextField txtcorreo;
    @FXML
    private PasswordField txtPassword;

    @FXML
    private void onLogin(ActionEvent e){
        String correo=txtcorreo.getText().trim();
        String pass=txtPassword.getText().trim();

        UsuarioDaoImpl dao=new UsuarioDaoImpl();
        try {
            if(dao.login(correo,pass)){
                System.out.println("Se pudo logear con Exito!");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/demo/view/dashboard.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);


            }else{
                showAlert("Error","Credenciales incorrectas");
                System.out.println("Credenciales incorrectas!");
            }

        }catch (Exception err){
            showAlert("Error", "Hubo un error en la aplicación");
            System.out.println(err.getMessage());
        }

    }

    @FXML
    private void onTest(ActionEvent e){

    }

    public void showAlert(String title, String msg){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
