package mx.edu.utez.biblioteca.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Ejemplar {
    private int idEjemplar;
    private String codigo;
    private String titulo;
    private String ubicacion;
    private final BooleanProperty seleccionado = new SimpleBooleanProperty(false);

    public Ejemplar() {

    }

    public Ejemplar(String codigo, String titulo, String ubicacion, int idEjemplar) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.idEjemplar = idEjemplar;
    }

    public int getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(int idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado.set(seleccionado);
    }
}