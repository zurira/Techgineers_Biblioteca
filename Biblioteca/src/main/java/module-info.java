module mx.edu.utez.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome; // <--- AÑADIDO: Necesario para los iconos "fa-pencil" etc.
    requires java.sql;
    requires java.desktop; // Mantén si lo necesitas, si no, puedes quitarlo.

    // Abrimos paquetes para reflexión (FXML Loaders y Controllers)
    opens mx.edu.utez.biblioteca to javafx.fxml; // Para HelloApplication y otros FXMLs en la raíz del paquete
    opens mx.edu.utez.biblioteca.controller to javafx.fxml; // Para tus controladores
    opens mx.edu.utez.biblioteca.model to javafx.base; // Para PropertyValueFactory en TableView

    // *** CRÍTICO: Abre el paquete CSS para el FXMLLoader ***
    opens mx.edu.utez.biblioteca.css to javafx.fxml;

    // *** CRÍTICO: Abre el paquete de vistas (donde están tus FXMLs) para el FXMLLoader ***
    // Asumiendo que tus FXMLs como Dashboard.fxml están en mx.edu.utez.biblioteca.views
    opens mx.edu.utez.biblioteca.views to javafx.fxml;


    // Exportamos los paquetes que otras partes de tu aplicación (fuera de este módulo)
    // o el lanzador de JavaFX necesitan acceder directamente.
    exports mx.edu.utez.biblioteca;
    exports mx.edu.utez.biblioteca.controller;
    exports mx.edu.utez.biblioteca.model; // Generalmente útil exportar los modelos
    exports mx.edu.utez.biblioteca.dao.impl; // Si tu DAO es usado directamente por otras clases fuera del módulo
    exports mx.edu.utez.biblioteca.config; // Si tu configuración es usada directamente por otras clases fuera del módulo
}