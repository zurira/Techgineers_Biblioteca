package mx.edu.utez.biblioteca.model;

public class Libro {
    private int id;
    private String titulo;
    private String isbn;
    private String resumen;
    private int anioPublicacion;
    private String portada;
    private String estado; // Restaurado
    private Editorial editorial;
    private Autor autor;
    private Categoria categoria;

    // Constructores
    public Libro() { }

    public Libro(int id, String titulo, String isbn, String resumen, int anioPublicacion, String portada, String estado, Editorial editorial, Autor autor, Categoria categoria) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.resumen = resumen;
        this.anioPublicacion = anioPublicacion;
        this.portada = portada;
        this.estado = estado;
        this.editorial = editorial;
        this.autor = autor;
        this.categoria = categoria;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Editorial getEditorial() {
        return editorial;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    // Métodos para facilitar la visualización en la tabla (si se usan)
    public String getEditorialNombre() {
        return editorial != null ? editorial.getNombre() : "N/A";
    }

    public String getAutorNombre() {
        return autor != null ? autor.getNombreCompleto() : "N/A";
    }

    public String getCategoriaNombre() {
        return categoria != null ? categoria.getNombre() : "N/A";
    }

    @Override
    public String toString() {
        return titulo;
    }
}