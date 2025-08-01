package mx.edu.utez.biblioteca.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.edu.utez.biblioteca.dao.impl.AutorDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.CategoriaDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.EditorialDaoImpl;
import mx.edu.utez.biblioteca.dao.impl.LibroDaoImpl;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.model.Libro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LibroFormController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtIsbn;
    @FXML
    private TextArea txtResumen;
    @FXML
    private TextField txtAnioPublicacion;
    @FXML
    private ImageView coverImageView;
    @FXML
    private ComboBox<Editorial> cmbEditorial;
    @FXML
    private ComboBox<Autor> cmbAutor;
    @FXML
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<String> cmbEstado;

    private Stage dialogStage;
    private Libro libro;
    private boolean isClicked = false;

    private LibroDaoImpl libroDao;
    private AutorDaoImpl autorDao;
    private EditorialDaoImpl editorialDao;
    private CategoriaDaoImpl categoriaDao;

    private String selectedImagePath;
    // La ruta de la imagen por defecto ya no se usa, pero la carpeta de covers sí
    // private static final String DEFAULT_COVER_PATH = "/images/default_cover.png";
    private static final String COVERS_DIRECTORY = "covers";

    public LibroFormController() {
        this.libroDao = new LibroDaoImpl();
        this.autorDao = new AutorDaoImpl();
        this.editorialDao = new EditorialDaoImpl();
        this.categoriaDao = new CategoriaDaoImpl();
    }

    @FXML
    private void initialize() {
        ObservableList<String> estados = FXCollections.observableArrayList("A", "I");
        cmbEstado.setItems(estados);
        cmbEstado.getSelectionModel().selectFirst();

        cmbEditorial.setCellFactory(lv -> new ListCell<Editorial>() {
            @Override
            protected void updateItem(Editorial item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombre() : ""));
            }
        });
        cmbEditorial.setButtonCell(new ListCell<Editorial>() {
            @Override
            protected void updateItem(Editorial item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombre() : ""));
            }
        });

        cmbAutor.setCellFactory(lv -> new ListCell<Autor>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombreCompleto() : ""));
            }
        });
        cmbAutor.setButtonCell(new ListCell<Autor>() {
            @Override
            protected void updateItem(Autor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombreCompleto() : ""));
            }
        });

        cmbCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombre() : ""));
            }
        });
        cmbCategoria.setButtonCell(new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : (item != null ? item.getNombre() : ""));
            }
        });

        // Ya no se carga la imagen por defecto aquí. El ImageView estará vacío por defecto.
        if (coverImageView == null) {
            System.err.println("Error: coverImageView es null en initialize(). Revisa tu FXML.");
        }

        loadComboBoxData();
    }

    public void setDialogStage(Stage dialogStage) {
        if (dialogStage == null) {
            System.err.println("Advertencia: Se intentó establecer un dialogStage nulo.");
            return;
        }
        this.dialogStage = dialogStage;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;

        if (libro.getId() != 0) {
            txtTitulo.setText(libro.getTitulo());
            txtIsbn.setText(libro.getIsbn());
            txtResumen.setText(libro.getResumen());
            txtAnioPublicacion.setText(String.valueOf(libro.getAnioPublicacion()));

            if (coverImageView != null) {
                if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                    File file = new File(COVERS_DIRECTORY + File.separator + libro.getPortada());
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        coverImageView.setImage(image);
                        selectedImagePath = file.getAbsolutePath();
                    } else {
                        System.err.println("La imagen de portada no se encontró en: " + file.getAbsolutePath());
                        // Si la imagen no se encuentra, el ImageView se quedará vacío
                        coverImageView.setImage(null); // Limpiar la imagen si no se encuentra
                        selectedImagePath = null;
                    }
                } else {
                    // Si no hay portada definida para el libro, el ImageView se quedará vacío
                    coverImageView.setImage(null); // Limpiar la imagen
                    selectedImagePath = null;
                }
            } else {
                System.err.println("Advertencia: coverImageView es null en setLibro(). No se puede cargar la imagen.");
            }

            cmbEditorial.getSelectionModel().select(libro.getEditorial());
            cmbAutor.getSelectionModel().select(libro.getAutor());
            cmbCategoria.getSelectionModel().select(libro.getCategoria());
            cmbEstado.getSelectionModel().select(libro.getEstado());

        } else {
            selectedImagePath = null;
            cmbEstado.getSelectionModel().select("A");
            txtAnioPublicacion.setText("");
            // Para un nuevo libro, asegurarse de que el ImageView esté vacío
            if (coverImageView != null) {
                coverImageView.setImage(null);
            }
        }
    }

    public boolean isOkClicked() {
        return isClicked;
    }

    private void loadComboBoxData() {
        try {
            List<Editorial> editoriales = editorialDao.findAll();
            cmbEditorial.setItems(FXCollections.observableArrayList(editoriales));

            List<Autor> autores = autorDao.findAll();
            cmbAutor.setItems(FXCollections.observableArrayList(autores));

            List<Categoria> categorias = categoriaDao.findAll();
            cmbCategoria.setItems(FXCollections.observableArrayList(categorias));

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar los datos de las listas",
                    "Hubo un error al intentar obtener los datos de autores, editoriales o categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) {
            errorMessage += "¡No se ha ingresado el título!\n";
        }
        if (txtIsbn.getText() == null || txtIsbn.getText().trim().isEmpty()) {
            errorMessage += "¡No se ha ingresado el ISBN!\n";
        }
        if (txtResumen.getText() == null || txtResumen.getText().trim().isEmpty()) {
            errorMessage += "¡No se ha ingresado el resumen!\n";
        }

        if (txtAnioPublicacion.getText() == null || txtAnioPublicacion.getText().trim().isEmpty()) {
            errorMessage += "¡No se ha ingresado el año de publicación!\n";
        } else {
            try {
                int year = Integer.parseInt(txtAnioPublicacion.getText().trim());
                int currentYear = LocalDate.now().getYear();
                if (year <= 0 || year > currentYear + 5) {
                    errorMessage += "¡El año de publicación no es válido (debe ser un número positivo y no muy futuro)! (Ej: 1900 - " + (currentYear + 5) + ")\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "¡El año de publicación debe ser un número válido!\n";
            }
        }

        if (cmbEditorial.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "¡No se ha seleccionado la editorial!\n";
        }
        if (cmbAutor.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "¡No se ha seleccionado el autor!\n";
        }
        if (cmbCategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "¡No se ha seleccionado la categoría!\n";
        }
        if (cmbEstado.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "¡No se ha seleccionado el estado!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Campos Inválidos", "Por favor, corrige los campos inválidos", errorMessage);
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onSelectImage(ActionEvent event) {
        if (coverImageView == null) {
            System.err.println("Error: coverImageView es null. No se puede seleccionar la imagen.");
            showAlert(Alert.AlertType.ERROR, "Error de UI", "Componente de Imagen No Disponible",
                    "El componente de visualización de imagen no está inicializado. Por favor, reinicia la aplicación.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                coverImageView.setImage(image);
                selectedImagePath = file.getAbsolutePath();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error de Imagen", "No se pudo cargar la imagen",
                        "Hubo un error al cargar la imagen seleccionada: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onSave(ActionEvent event) {
        if (isInputValid()) {
            try {
                String coverFileName = null;
                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    File sourceFile = new File(selectedImagePath);
                    if (sourceFile.exists()) {
                        coverFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
                        Path targetPath = Paths.get(COVERS_DIRECTORY, coverFileName);
                        Files.createDirectories(targetPath.getParent());
                        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.err.println("La imagen seleccionada no existe: " + selectedImagePath);
                        showAlert(Alert.AlertType.WARNING, "Advertencia", "Imagen no encontrada",
                                "La imagen seleccionada no pudo ser encontrada. Se guardará sin portada o con la existente.");
                    }
                } else {
                    coverFileName = null;
                }

                libro.setTitulo(txtTitulo.getText().trim());
                libro.setIsbn(txtIsbn.getText().trim());
                libro.setResumen(txtResumen.getText().trim());
                libro.setAnioPublicacion(Integer.parseInt(txtAnioPublicacion.getText().trim()));
                libro.setPortada(coverFileName);
                libro.setEditorial(cmbEditorial.getSelectionModel().getSelectedItem());
                libro.setAutor(cmbAutor.getSelectionModel().getSelectedItem());
                libro.setCategoria(cmbCategoria.getSelectionModel().getSelectedItem());
                libro.setEstado(cmbEstado.getSelectionModel().getSelectedItem());

                if (libro.getId() == 0) {
                    libroDao.create(libro);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro Guardado", "El libro ha sido agregado exitosamente.");
                } else {
                    libroDao.update(libro);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Libro Actualizado", "El libro ha sido actualizado exitosamente.");
                }

                isClicked = true;
                if (dialogStage != null) {
                    dialogStage.close();
                } else {
                    System.err.println("Advertencia: dialogStage es null en onSave(). No se puede cerrar la ventana.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error de Base de Datos", "Error al guardar el libro",
                        "Hubo un error al interactuar con la base de datos: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error de Archivo", "Error al guardar la portada",
                        "Hubo un error al copiar la imagen de portada: " + e.getMessage());
                e.printStackTrace();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error de Formato", "Año de Publicación Inválido",
                        "Por favor, asegúrate de que el año de publicación sea un número válido.");
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error inesperado",
                        "Detalles: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            System.err.println("Advertencia: dialogStage es null en onCancel(). No se puede cerrar la ventana.");
        }
    }
}