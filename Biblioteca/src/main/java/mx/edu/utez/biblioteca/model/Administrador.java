
package mx.edu.utez.biblioteca.model;


public class Administrador {
    private int id;
    private String nombreCompleto;
    private String usuario;
    private String correo;
    private String telefono;
    private String contrasena;
    private String rol;
    private String direccion;
    private String imagen; // puedes usar String para la ruta o cambiarlo por byte[] si usas BLOB
    private boolean estado;

    public Administrador(int id, String nombreCompleto, String correo, String contrasena, boolean estado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.estado = estado;
    }


    public Administrador(int id, String nombreCompleto, String usuario, String correo, String telefono,
                         String contrasena, String rol, String direccion, String imagen, boolean estado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.rol = rol;
        this.direccion = direccion;
        this.imagen = imagen;
        this.estado = estado;
    }



    // Getters
    public int getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getUsuario() { return usuario; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getDireccion() { return direccion; }
    public String getImagen() { return imagen; }
    public boolean getEstado() { return estado; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setRol(String rol) { this.rol = rol; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
