package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.util.List;

public interface IUsuarioBiblioteca {
    public int obtenerIdPorNombre(String nombre);
    public List<UsuarioBiblioteca> findAll();
}

