
package mx.edu.utez.biblioteca.model;

public class Administrador {
    private int id;
    private String nombreCompleto;
    private String usuario;
    private String correo;
    private boolean estado;

    public Administrador(int id, String nombreCompleto, String usuario, String correo, boolean estado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.correo = correo;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getUsuario() { return usuario; }
    public String getCorreo() { return correo; }
    public boolean getEstado() { return estado; }

    public void setId(int id) { this.id = id; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setEstado(boolean estado) { this.estado = estado; }
}


