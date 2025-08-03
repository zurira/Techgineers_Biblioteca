package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Categoria;
import java.util.List;

public interface ICategoria {
    List<Categoria> findAll() throws Exception;
    Categoria findById(int id) throws Exception;
    void create(Categoria categoria) throws Exception;
    void update(Categoria categoria) throws Exception;
    void delete(int id) throws Exception;
}
