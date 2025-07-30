package mx.edu.utez.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

            // Lógica para mostrar o no la imagen de medalla
            if (i < 3) { // Solo para los 3 primeros puestos (0, 1, 2)
                ImageView medalImageView = new ImageView();
                medalImageView.setFitWidth(38); // Usar el mismo tamaño que en el CSS
                medalImageView.setFitHeight(38); // Usar el mismo tamaño que en el CSS
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
                // Para los puestos 4 y 5, añade un Label vacío para mantener la alineación
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
            case 0: return "/mx/edu/utez/biblioteca/img/gold_medal.png";
            case 1: return "/mx/edu/utez/biblioteca/img/silver_medal.png";
            case 2: return "/mx/edu/utez/biblioteca/img/bronze_medal.png";
            default: return ""; // No se usará, pero el switch requiere un default
        }
    }
}