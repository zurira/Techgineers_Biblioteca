package mx.edu.utez.biblioteca.model;

import java.time.LocalDate;

public class Prestamo {
    private String usuarioNombre;
    private String correo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private String estado;

    public Prestamo(String usuarioNombre, String correo, LocalDate fechaPrestamo, LocalDate fechaLimite, String estado) {
        this.usuarioNombre = usuarioNombre;
        this.correo = correo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
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
}


