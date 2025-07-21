package mx.edu.utez.biblioteca.model;

public class Libro {
    private int id;
    private String titulo;
    private String isbn;
    private String resumen;
    private int anioPublicacion;
    private String portada;
    private String Editorial;
    private Autor autor;
    private Editorial editorial;
    private Categoria categoria;


    public Libro() {
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

    public String getIdEditorial() {
        return Editorial;
    }

    public void setIdEditorial(String Editorial) {
        this.Editorial = Editorial;
    }

    public String getEditorial() {
        return Editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public void setEditorial(String editorial) {
        Editorial = editorial;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
                ", Editorial=" + Editorial +
                
                '}';
    }
    //modelado terminado
}
