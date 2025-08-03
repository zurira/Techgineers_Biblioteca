package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Editorial;
import java.util.List;

public interface IEditorial {
    List<Editorial> findAll() throws Exception;
    Editorial findById(int id) throws Exception;
    void create(Editorial editorial) throws Exception;
    void update(Editorial editorial) throws Exception;
    void delete(int id) throws Exception;
}
