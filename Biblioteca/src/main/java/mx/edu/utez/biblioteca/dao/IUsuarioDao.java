package mx.edu.utez.biblioteca.dao;

public interface IUsuarioDao {
    public boolean login(String correo, String pass) throws Exception;
}
