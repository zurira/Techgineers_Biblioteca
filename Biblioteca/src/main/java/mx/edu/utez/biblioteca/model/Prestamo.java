package mx.edu.utez.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    private int id;
    private String usuarioNombre;
    private String correo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaReal;
    private String estado;
    private int idUsuario;
    private int idEjemplar;
    private Libro libro;
    private UsuarioBiblioteca usuario;
    private Ejemplar ejemplar;

    private double multa; // ← esta propiedad debe existir

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public double getMulta() {
        return multa;
    }

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

    public Prestamo(int id, String usuarioNombre, String correo, LocalDate fechaPrestamo, String estado, LocalDate fechaReal, LocalDate fechaLimite, int idUsuario, int idEjemplar) {
        this.id = id;
        this.usuarioNombre = usuarioNombre;
        this.correo = correo;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = estado;
        this.fechaReal = fechaReal;
        this.fechaLimite = fechaLimite;
        this.idUsuario = idUsuario;
        this.idEjemplar = idEjemplar;
    }

    public Prestamo() {
    }

    public double calcularMulta(double tarifaPorDia) {
        LocalDate fechaEvaluacion = (fechaReal != null) ? fechaReal : LocalDate.now();
        long diasRetraso = ChronoUnit.DAYS.between(fechaLimite, fechaEvaluacion);
        return diasRetraso > 0 ? diasRetraso * tarifaPorDia : 0.0;
    }

    public String calcularEstado(LocalDate fechaActual, double tarifaMulta) {
        if (fechaReal != null) {
            if (calcularMulta(tarifaMulta) > 0) {
                return "Finalizado";
            } else {
                return "Finalizado";
            }
        }

        if (fechaActual.isAfter(fechaLimite)) {
            return "Retrasado";
        }

        return "Activo";
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

    public int getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(int idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }
}

