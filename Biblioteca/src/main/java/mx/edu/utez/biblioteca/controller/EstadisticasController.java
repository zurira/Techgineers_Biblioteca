package mx.edu.utez.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.IEstadisticas;
import mx.edu.utez.biblioteca.dao.impl.EstadisticasDaoImpl;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class EstadisticasController implements Initializable {

    @FXML
    private VBox vboxTopBooks;

    @FXML
    private VBox vboxTopClients;

    private IEstadisticas estadisticasDao;
    @FXML private Button btnlogout;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        estadisticasDao = new EstadisticasDaoImpl();
        loadStatistics();
    }

    private void loadStatistics() {
        // Cargar libros más prestados
        List<Map<String, Object>> topBooks = estadisticasDao.getTop5MostBorrowedBooks();
        displayTopItems(vboxTopBooks, topBooks, "titulo", "No se encontraron libros prestados en el último mes.");

        // Cargar clientes con más préstamos
        List<Map<String, Object>> topClients = estadisticasDao.getTop5MostActiveClients();
        displayTopItems(vboxTopClients, topClients, "nombre_usuario", "No se encontraron clientes con préstamos en el último mes.");
    }

    private void displayTopItems(VBox container, List<Map<String, Object>> items, String nameKey, String noResultsMessage) {
        if (items.isEmpty()) {
            Label noResultsLabel = new Label(noResultsMessage);
            noResultsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-alignment: center; -fx-max-width: Infinity;");
            container.getChildren().add(noResultsLabel);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            Map<String, Object> item = items.get(i);
            String itemName = (String) item.get(nameKey);
            HBox itemBox = new HBox();
            itemBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            itemBox.setSpacing(10);
            itemBox.getStyleClass().add("medal-item");

            if (i < 5) {
                ImageView medalImageView = new ImageView();
                medalImageView.setFitWidth(45);
                medalImageView.setFitHeight(45);
                try {
                    String medalPath = getMedalImagePath(i);
                    Image medalImage = new Image(getClass().getResourceAsStream(medalPath));
                    if (medalImage.isError()) {
                        throw new RuntimeException("Error al cargar la imagen: " + medalPath);
                    }
                    medalImageView.setImage(medalImage);
                } catch (Exception e) {
                    System.err.println("Error al cargar imagen de medalla " + i + ": " + e.getMessage());
                }
                medalImageView.getStyleClass().add("medal-image");
                itemBox.getChildren().add(medalImageView);
            } else {
                Label emptySpaceLabel = new Label();
                emptySpaceLabel.setPrefWidth(38);
                emptySpaceLabel.setPrefHeight(38);
                itemBox.getChildren().add(emptySpaceLabel);
            }

            Label itemLabel = new Label(itemName);
            itemLabel.getStyleClass().add("medal-text");

            itemBox.getChildren().add(itemLabel);
            container.getChildren().add(itemBox);
        }
    }

    private String getMedalImagePath(int index) {
        switch (index) {
            case 0: return "/mx/edu/utez/biblioteca/img/first.png";
            case 1: return "/mx/edu/utez/biblioteca/img/second.png";
            case 2: return "/mx/edu/utez/biblioteca/img/third.png";
            case 3: return "/mx/edu/utez/biblioteca/img/fourth.png";
            case 4: return "/mx/edu/utez/biblioteca/img/fifth.png";
            default: return "";
        }
    }

    @FXML
    private void irUsuarios(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Usuarios.fxml"));
            Region root = (Region) loader.load();

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

    @FXML
    private void irPrestamos(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/Prestamo.fxml"));
            Region root = (Region) loader.load();

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

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/ModalCerrarSesion.fxml"));
            Parent modalRoot = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle("¿Cerrar sesión?");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();

            if (ModalCerrarSesionController.cerrarSesionConfirmado) {
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/mx/edu/utez/biblioteca/views/login.fxml"));
                Parent loginRoot = loginLoader.load();

                Stage currentStage = (Stage) btnlogout.getScene().getWindow();
                currentStage.setScene(new Scene(loginRoot, 600, 400));
                currentStage.setTitle("Inicio de sesión");
            }

            ModalCerrarSesionController.cerrarSesionConfirmado = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}