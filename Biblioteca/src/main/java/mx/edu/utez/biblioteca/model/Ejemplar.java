package mx.edu.utez.biblioteca.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Ejemplar {
    private int idEjemplar;
    private String codigo;
    private String titulo;
    private String ubicacion;
    private BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    public Ejemplar() {
    }

    public Ejemplar(String codigo, String titulo, String ubicacion, BooleanProperty seleccionado, int idEjemplar) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.seleccionado = seleccionado;
        this.idEjemplar = idEjemplar;
    }
}
