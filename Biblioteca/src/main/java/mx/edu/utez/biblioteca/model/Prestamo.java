package mx.edu.utez.biblioteca.model;

import java.time.LocalDate;

public class Prestamo {
    private int id;
    private String usuarioNombre;
    private String correo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaReal;
    private String estado;
    private int idUsuario;
    private Libro libro;
    private UsuarioBiblioteca usuario;

    public UsuarioBiblioteca getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioBiblioteca usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Prestamo(int id, String usuarioNombre, String correo, LocalDate fechaPrestamo, LocalDate fechaLimite, String estado, LocalDate fechaDevolucion) {
        this.id = id;
        this.usuarioNombre = usuarioNombre;
        this.correo = correo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.fechaReal = fechaDevolucion;
    }

    public Prestamo() {
    }

    public int getId() {
        return id;
    }

    public LocalDate getFechaReal() {
        return fechaReal;
    }

    public void setFechaReal(LocalDate fechaDevolucion) {
        this.fechaReal = fechaDevolucion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}
