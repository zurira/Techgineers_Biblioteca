package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Usuario;

public interface IUsuarioSistema {
    public Usuario login(String correo, String pass) throws Exception;
}
