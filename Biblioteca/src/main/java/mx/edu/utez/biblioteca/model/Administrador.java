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
    }




