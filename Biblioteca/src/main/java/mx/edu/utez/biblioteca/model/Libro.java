package mx.edu.utez.biblioteca.model;

import java.util.List;
//Clase de libro y aqui agregamos los getters y setters
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
    private String estado;

    // Constructor vacío
    public Libro() {
    }

    // Constructor existente, actualizado para incluir 'estado'
    public Libro(int id, String titulo, String isbn, String resumen, int anioPublicacion, String portada,
                 Autor autor, Editorial editorial, Categoria categoria, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.resumen = resumen;
        this.anioPublicacion = anioPublicacion;
        this.portada = portada;
        this.autor = autor;
        this.editorial = editorial;
        this.categoria = categoria;
        this.estado = estado;
    }

    // Getters y Setters
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
                ", autor=" + (autor != null ? autor.getNombreCompleto() : "N/A") +
                ", editorial=" + (editorial != null ? editorial.getNombre() : "N/A") +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "N/A") +
                ", estado='" + estado + '\'' +
                '}';
    }
}