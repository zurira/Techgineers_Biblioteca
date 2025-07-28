package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.util.List;

public interface IUsuarioBiblioteca {
    List<UsuarioBiblioteca> findAll() throws Exception;
    UsuarioBiblioteca findById(int id) throws Exception;
    void create(UsuarioBiblioteca usuario) throws Exception;
    void update(UsuarioBiblioteca usuario) throws Exception;
    void delete(int id) throws Exception;
    List<UsuarioBiblioteca> search(String searchTerm) throws Exception;
}
