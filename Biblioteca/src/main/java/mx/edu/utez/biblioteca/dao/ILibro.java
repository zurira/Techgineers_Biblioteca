package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Libro;
import java.util.List;


public interface ILibro {
    List<Libro> findAll() throws Exception;
    Libro findById(int id) throws Exception;
    public void create(Libro libro) throws Exception;
    void update(Libro libro) throws Exception;
    void delete(int id) throws Exception;
    List<Libro> search(String searchTerm) throws Exception;
    public List<Libro> obtenerLibros();

    Libro findByIsbn(String isbn) throws Exception;

    // Método para actualizar el estado del libro
    void updateStatus(int id, String estado) throws Exception;
}
