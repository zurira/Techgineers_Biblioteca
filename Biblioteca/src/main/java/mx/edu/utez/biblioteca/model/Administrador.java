
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


