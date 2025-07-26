package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Usuario;

import java.util.List;

public interface IUsuario {
    public int obtenerIdPorNombre(String nombre);
    public List<String> obtenerTodosLosNombres();
}
