package mx.edu.utez.biblioteca.controller;

public class EditarUsuarioController {
    @FXML private TextField txtNombre, txtEmail, txtTelefono;
    @FXML private DatePicker dateNacimiento;
    @FXML private TextArea txtDireccion;

    @FXML
    private void guardar() {
        // Aquí puedes guardar el usuario (o actualizarlo)
        System.out.println("Guardado: " + txtNombre.getText());
        cerrar();
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }
}


}
