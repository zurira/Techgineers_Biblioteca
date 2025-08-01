package mx.edu.utez.biblioteca.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// No se necesita LocalDate si ANIO_PUBLICACION es NUMBER en la BD
// import java.time.LocalDate;

public class Libro {
    private String titulo, isbn, sinopsis, estado, autor, categoria, editorial, portada;
    private int anioPublicacion;

    public Libro(String titulo, String isbn, String sinopsis, int anioPublicacion, String estado,
                 String autor, String categoria, String editorial, String portada) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.sinopsis = sinopsis;
        this.anioPublicacion = anioPublicacion;
        this.estado = estado;
        this.autor = autor;
        this.categoria = categoria;
        this.editorial = editorial;
        this.portada = portada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", estado='" + estado + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial='" + editorial + '\'' +
                ", portada='" + portada + '\'' +
                '}';
    }
}
