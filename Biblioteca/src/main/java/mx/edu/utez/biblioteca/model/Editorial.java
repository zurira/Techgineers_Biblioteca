package mx.edu.utez.biblioteca.model;

import java.util.Objects;

public class Editorial {
    private int id;
    private String nombre;

    public Editorial() {
    }

    public Editorial(int id, String nombre) {
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
        Editorial editorial = (Editorial) o;
        if (this.id != 0 && editorial.id != 0) {
            return id == editorial.id;
        }
        return Objects.equals(nombre.toUpperCase(), editorial.nombre.toUpperCase());
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(nombre.toUpperCase());
    }
}