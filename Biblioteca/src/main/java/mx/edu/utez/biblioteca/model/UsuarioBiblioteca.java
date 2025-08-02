package mx.edu.utez.biblioteca.model;

import java.time.LocalDate;

public class UsuarioBiblioteca {
    private int id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private String direccion;
    private String estado;
    private byte[] fotografia;

    public UsuarioBiblioteca(){

    }

    public UsuarioBiblioteca(int id, String nombre, LocalDate fechaNacimiento, String correo, String telefono, String direccion, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
        this.direccion = direccion;
    }

    public UsuarioBiblioteca(String nombre, LocalDate fechaNacimiento, String correo, String telefono, String direccion, String estado) {
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.estado = estado;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public byte[] getFotografia() {
        return fotografia;
    }

    public void setFotografia(byte[] fotografia) {
        this.fotografia = fotografia;
    }

    @Override
    public String toString() {
        return nombre;
    }
}