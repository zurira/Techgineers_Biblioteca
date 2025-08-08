package mx.edu.utez.biblioteca.model;

import java.util.Objects;

public class Autor {
    private int id;
    private String nombreCompleto;

    public Autor() {
    }

    public Autor(int id, String nombreCompleto) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    @Override
    public String toString() {
        return nombreCompleto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        // Si ambos tienen ID, comparamos por ID. Si no, por nombre (case-insensitive).
        if (this.id != 0 && autor.id != 0) {
            return id == autor.id;
        }
        return Objects.equals(nombreCompleto.toUpperCase(), autor.nombreCompleto.toUpperCase());
    }

    @Override
    public int hashCode() {
        // Usar ID para hashCode si está disponible, de lo contrario, el nombre (case-insensitive)
        if (this.id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(nombreCompleto.toUpperCase());
    }
}