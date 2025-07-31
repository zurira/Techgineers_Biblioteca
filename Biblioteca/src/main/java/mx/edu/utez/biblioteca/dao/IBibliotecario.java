package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Bibliotecario;
import java.util.List;

public interface IBibliotecario {
    List<Bibliotecario> findAll() throws Exception;
    Bibliotecario findById(int id) throws Exception;
    boolean create(Bibliotecario bibliotecario) throws Exception;
    boolean update(Bibliotecario bibliotecario) throws Exception;
    boolean delete(int id) throws Exception;
    List<Bibliotecario> search(String searchTerm) throws Exception;
    boolean updateStatus(int idBibliotecario, String estado) throws Exception;

    // Método para buscar un bibliotecario por su username para el login
    Bibliotecario findByUsername(String username) throws Exception;
}
