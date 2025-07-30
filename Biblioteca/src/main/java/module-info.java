module mx.edu.utez.biblioteca {
    // Módulos de JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Añadido: A veces es necesario explícitamente

    // Módulos de librerías externas
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome; // <--- IMPRESCINDIBLE si usas iconos FontAwesome como "fa-pencil"

    // Módulos de Java Standard Library (JDK)
    requires java.sql;      // Para JDBC y conexión a base de datos
    requires java.desktop;  // Mantener si necesitas funcionalidades de AWT/Swing (ej. FileChooser, ImageIO). Si no, puedes quitarlo.

    // === Apertura de paquetes para reflexión (FXMLLoader y JavaFX Runtime) ===
    // 'opens' permite que el sistema de módulos de JavaFX acceda a los componentes internos
    // de tus paquetes (ej. los campos @FXML en los controladores, o los recursos FXML/CSS).

    // Abre el paquete base de la aplicación para que FXMLLoader pueda acceder a HelloApplication y otros FXMLs en la raíz
    opens mx.edu.utez.biblioteca to javafx.fxml;

    // Abre el paquete de controladores para que FXMLLoader pueda instanciar y asignar los @FXML
    opens mx.edu.utez.biblioteca.controller to javafx.fxml;

    // Abre el paquete de modelos si usas PropertyValueFactory en TableView u otras clases de JavaFX que accedan a sus propiedades
    opens mx.edu.utez.biblioteca.model to javafx.base;

    // Abre el paquete de VISTAS donde están tus archivos FXML. Es CRÍTICO para que el FXMLLoader los encuentre.
    // Esto es vital porque tu FXML está en 'mx.edu.utez.biblioteca.views'
    opens mx.edu.utez.biblioteca.views to javafx.fxml;

    // Abre el paquete de CSS para que FXMLLoader o el ClassLoader puedan acceder a tus hojas de estilo.
    // Aunque se cargue programáticamente, es buena práctica abrirlo si lo vas a usar.
    opens mx.edu.utez.biblioteca.css to javafx.fxml;

    // === Exportación de paquetes ===
    // 'exports' permite que otros módulos (si tuvieras) o el lanzador de JavaFX accedan a tus clases públicas.

    // Exporta el paquete principal de la aplicación
    exports mx.edu.utez.biblioteca;
    // Exporta tus controladores si van a ser usados o extendidos por código fuera de este módulo
    exports mx.edu.utez.biblioteca.controller;
    // Exporta tus modelos si van a ser usados por clases en otros módulos (ej. DAO, servicios, etc.)
    exports mx.edu.utez.biblioteca.model;
    // Si tienes DAOs o configs que otras partes de tu aplicación necesitan acceder, exporta también:
    // exports mx.edu.utez.biblioteca.dao.impl;
    // exports mx.edu.utez.biblioteca.config;
}