package mx.edu.utez.biblioteca.dao;

public interface IUsuario {
    public boolean login(String correo, String pass) throws Exception;
}
