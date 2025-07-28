package mx.edu.utez.biblioteca.dao;
<<<<<<< HEAD
import mx.edu.utez.biblioteca.model.Usuario;

public interface IUsuario {
    public Usuario login(String correo, String pass) throws Exception;
=======

import mx.edu.utez.biblioteca.model.Usuario;

import java.util.List;

public interface IUsuario {
    public int obtenerIdPorNombre(String nombre);
    public List<String> obtenerTodosLosNombres();
>>>>>>> TTS17
}
