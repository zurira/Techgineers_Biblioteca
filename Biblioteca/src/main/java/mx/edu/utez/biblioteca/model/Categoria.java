package mx.edu.utez.biblioteca.model;

import java.util.Objects;

public class Categoria {
    private int id;
    private String nombre;

    public Categoria() {
    }

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        if (this.id != 0 && categoria.id != 0) {
            return id == categoria.id;
        }
        return Objects.equals(nombre.toUpperCase(), categoria.nombre.toUpperCase());
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(nombre.toUpperCase());
    }
}
