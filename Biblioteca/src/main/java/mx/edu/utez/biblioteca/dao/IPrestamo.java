package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Prestamo;
import java.util.List;

public interface IPrestamo {
    List<Prestamo> findAll() throws Exception; // Metodo para obtener todos los préstamos
    Prestamo findById(int id) throws Exception;
    void create(Prestamo prestamo) throws Exception;
    void update(Prestamo prestamo) throws Exception;
    void delete(int id) throws Exception; // Metodo para eliminar por ID
    List<Prestamo> search(String searchTerm) throws Exception;
}