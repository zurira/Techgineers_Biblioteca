package mx.edu.utez.biblioteca.model;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private Libro libro;
    private UsuarioBiblioteca usuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaReal;
    private String estado;

    public Prestamo() {
    }

    public Prestamo(int id, Libro libro, UsuarioBiblioteca usuario, LocalDate fechaPrestamo, LocalDate fechaLimite, LocalDate fechaReal, String estado) {
        this.id = id;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaReal = fechaReal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public UsuarioBiblioteca getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioBiblioteca usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDate getFechaReal() {
        return fechaReal;
    }

    public void setFechaReal(LocalDate fechaReal) {
        this.fechaReal = fechaReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", libro=" + (libro != null ? libro.getTitulo() : "N/A") + // Asegura que no sea null
                ", usuario=" + (usuario != null ? usuario.getNombre() : "N/A") + // Asegura que no sea null
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaLimite=" + fechaLimite +
                ", fechaReal=" + fechaReal +
                ", estado='" + estado + '\'' +
                '}';
    }
}