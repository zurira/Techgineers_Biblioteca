package mx.edu.utez.biblioteca.dao;

import javafx.collections.ObservableList;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.util.List;

public interface IPrestamo {
    List<Prestamo> findAll() throws Exception; // Metodo para obtener todos los préstamos
    Prestamo findById(int id) throws Exception;
    void create(Prestamo prestamo) throws Exception;
    boolean update(Prestamo prestamo) throws Exception;
    List<Prestamo> search(String searchTerm) throws Exception;
    public ObservableList<UsuarioBiblioteca> obtenerUsuarios();
}

