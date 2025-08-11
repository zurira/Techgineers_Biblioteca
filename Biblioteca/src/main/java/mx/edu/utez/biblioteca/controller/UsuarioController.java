package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.biblioteca.model.Usuario;
import org.kordamp.ikonli.javafx.FontIcon;

public class UsuarioController {
    @FXML
    private TextField txtcorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private StackPane loginPane;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private Button togglePasswordBtn;

    @FXML
    private void onLogin(ActionEvent e){
        String input=txtcorreo.getText().trim();
        String pass = txtPassword.isVisible() ? txtPassword.getText().trim() : txtPasswordVisible.getText().trim();

        if (input.isEmpty() || pass.isEmpty()) {
            showAlert("Advertencia", "Los campos de correo/usuario y contraseña no pueden estar vacíos.");
            return;
        }

        // Validación de dominio de correo electrónico
        if (input.contains("@")) {
            String domain = input.substring(input.indexOf("@") + 1).toLowerCase();
            if (!"superadministrador.com".equals(domain) && !"administrador.com".equals(domain) && !"bibliotecario.com".equals(domain)) {
                showAlert("Acceso denegado", "Solo personal autorizado puede ingresar.");
                return;
            }
        }

        UsuarioDaoImpl dao=new UsuarioDaoImpl();
        try {
            Usuario usuario = dao.login(input,pass);
            if(usuario != null){
                System.out.println("Se pudo logear con Exito como: " + usuario.getRol().getNombre());

                FXMLLoader loader;
                switch (usuario.getRol().getNombre().trim().toUpperCase()) {
                    case "SUPERADMINISTRADOR":
                        System.out.println("Cargando vista de superadministrador");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/superadmin-view.fxml"));
                        break;
                    case "ADMINISTRADOR":
                        System.out.println("Cargando vista de administrador");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/estadisticasAdmin.fxml"));
                        break;
                    case "BIBLIOTECARIO":
                        System.out.println("Cargando vista de bibliotecario");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Estadisticas.fxml"));
                        break;
                    default:
                        showAlert("Error", "Rol no reconocido.");
                        return;
                }

                Region root = loader.load();
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                root.prefWidthProperty().bind(stage.widthProperty());
                root.prefHeightProperty().bind(stage.heightProperty());
                Scene scene = new Scene(root);
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();

            }else{
                showAlert("Error","Correo/Usuario y/o contraseña incorrectos.");
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

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        boolean isVisible = txtPasswordVisible.isVisible();

        if (isVisible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            if (togglePasswordBtn.getGraphic() instanceof FontIcon icon) {
                icon.setIconLiteral("fa-eye");
            }
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            if (togglePasswordBtn.getGraphic() instanceof FontIcon icon) {
                icon.setIconLiteral("fa-eye-slash");
            }
        }
    }

    @FXML
    private void irInicio(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/bienvenida.fxml"));
            Region root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            root.prefWidthProperty().bind(stage.widthProperty());
            root.prefHeightProperty().bind(stage.heightProperty());
            Scene scene = new Scene(root);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}