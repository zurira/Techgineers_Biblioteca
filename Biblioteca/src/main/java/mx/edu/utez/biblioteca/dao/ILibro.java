package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.model.Libro;

import java.util.List;

public interface ILibro {
    public List<Libro> obtenerLibros();
    public List<Libro> obtenerLibrosPorFiltro(String filtro, String categoria);
}
