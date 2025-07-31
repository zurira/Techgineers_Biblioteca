package mx.edu.utez.biblioteca.model;

public class Rol {
    private int id;
    private String nombre;

    // Constructores, getters y setters
    public Rol() {}

    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}