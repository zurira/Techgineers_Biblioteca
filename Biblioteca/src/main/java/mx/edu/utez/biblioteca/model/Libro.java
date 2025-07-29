package mx.edu.utez.biblioteca.model;
import java.util.List;

import java.util.List;

public class Libro {
    private int id;
    private String titulo;
    private String isbn;
    private String resumen;
    private int anioPublicacion;
    private String portada;
    private Autor autor;
    private Editorial editorial;
    private Categoria categoria;
    private List<Autor> autores;

    public Libro() {
    }

    public Libro(int id, String titulo, String resumen, int anioPublicacion, String urlPortada,
                 Autor autor, Editorial editorial) {
        this.id = id;
        this.titulo = titulo;
        this.resumen = resumen;
        this.anioPublicacion = anioPublicacion;
        this.portada = portada;
        this.autor = autor;
        this.editorial = editorial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", resumen='" + resumen + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", portada='" + portada + '\'' +
                ", Editorial=" + editorial +

                '}';
    }

}
