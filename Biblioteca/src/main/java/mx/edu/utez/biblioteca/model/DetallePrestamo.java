package mx.edu.utez.biblioteca.model;

public class DetallePrestamo {
    private String id;
    private String id_prestamo;
    private String id_ejemplar;
    private boolean develto;

    public DetallePrestamo() {
    }

    public DetallePrestamo(String id, String id_prestamo, String id_ejemplar, boolean develto) {
        this.id = id;
        this.id_prestamo = id_prestamo;
        this.id_ejemplar = id_ejemplar;
        this.develto = develto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(String id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public String getId_ejemplar() {
        return id_ejemplar;
    }

    public void setId_ejemplar(String id_ejemplar) {
        this.id_ejemplar = id_ejemplar;
    }

    public boolean isDevelto() {
        return develto;
    }

    public void setDevelto(boolean develto) {
        this.develto = develto;
    }
}
