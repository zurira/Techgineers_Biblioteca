package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.util.List;

public interface IUsuarioBiblioteca {
    List<UsuarioBiblioteca> findAll() throws Exception;
    UsuarioBiblioteca findById(int id) throws Exception;
    boolean create(UsuarioBiblioteca usuario) throws Exception;
    boolean update(UsuarioBiblioteca usuario) throws Exception;
    boolean delete(int id) throws Exception;
    List<UsuarioBiblioteca> search(String searchTerm) throws Exception;
    boolean existeNombre(String nombre) throws Exception;
}
