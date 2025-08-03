package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Autor;
import java.util.List;

public interface IAutor {
    List<Autor> findAll() throws Exception;
    Autor findById(int id) throws Exception;
    void create(Autor autor) throws Exception;
    void update(Autor autor) throws Exception;
    void delete(int id) throws Exception;
}