package mx.edu.utez.biblioteca.model;

public class Bibliotecario {
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String username;
    private String password;
    private byte[] foto;
    private Rol rol;
    private String estado;
    private String direccion;

    // Constructores, getters y setters
    public Bibliotecario() {}

    public Bibliotecario(int id, String nombre, String correo, String telefono, String username, String password, byte[] foto, Rol rol, String estado, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.username = username;
        this.password = password;
        this.foto = foto;
        this.rol = rol;
        this.estado = estado;
        this.direccion = direccion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getFoto() { return foto; }
    public void setFoto(byte[] foto) { this.foto = foto; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
