package mx.edu.utez.biblioteca.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.model.Usuario;

import java.io.ByteArrayInputStream;



public class ModalVerAdminController {



        @FXML private Label lblNombre;
        @FXML private Label lblUsuario;
        @FXML private Label lblCorreo;
        @FXML private Label lblTelefono;
        @FXML private Label lblDireccion;
        @FXML private Label lblRol;
        @FXML private Label lblEstado;
        @FXML private ImageView imgFoto;

        public void setUsuario(Usuario usuario) {
            lblNombre.setText(usuario.getNombre() != null ? usuario.getNombre() : "Sin nombre");
            lblUsuario.setText(usuario.getUsername() != null ? usuario.getUsername() : "Sin usuario");
            lblCorreo.setText(usuario.getCorreo() != null ? usuario.getCorreo() : "Sin correo");
            lblTelefono.setText(usuario.getTelefono() != null ? usuario.getTelefono() : "Sin teléfono");
            lblDireccion.setText(usuario.getDireccion() != null ? usuario.getDireccion() : "Sin dirección");
            lblRol.setText(usuario.getRol() != null ? usuario.getRol().getNombre() : "Sin rol");
            lblEstado.setText(usuario.getEstado() != null && usuario.getEstado().equalsIgnoreCase("S") ? "Activo" : "Inactivo");

            if (usuario.getFoto() != null && usuario.getFoto().length > 0) {
                Image image = new Image(new ByteArrayInputStream(usuario.getFoto()));
                imgFoto.setImage(image);
            } else {
                imgFoto.setImage(null); // O puedes poner una imagen por defecto si quieres
            }
        }

        @FXML
        private void cerrarVentana() {
            Stage stage = (Stage) lblNombre.getScene().getWindow();
            stage.close();
        }
    }






