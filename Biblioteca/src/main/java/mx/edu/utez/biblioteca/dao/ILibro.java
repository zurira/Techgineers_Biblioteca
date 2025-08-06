package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Libro;
import java.util.List;

public interface ILibro {
    List<Libro> findAll() throws Exception;
    Libro findById(int id) throws Exception;
    void create(Libro libro) throws Exception;
    void update(Libro libro) throws Exception;
    void delete(int id) throws Exception;
    List<Libro> search(String searchTerm) throws Exception;
    boolean updateStatus(int idLibro, String estado) throws Exception;


    // NUEVO MÉTODO AÑADIDO: Ahora la interfaz lo reconoce.

    Libro findByIsbn(String isbn) throws Exception;
}
