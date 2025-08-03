package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Usuario;

import java.util.List;

public interface IUsuario {
    public Usuario login(String correo, String pass) throws Exception;
    public List<Usuario> findAll() throws Exception;
    public Usuario findById(int id) throws Exception;
    public void create(Usuario usuario) throws Exception;
    public void update(Usuario usuario) throws Exception;
    public List<Usuario> findByRolNombre(String nombreRol) throws Exception;
}