package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Libro;

import java.util.List;

public class LibroDaoImpl implements ILibro {
    @Override
    public List<Libro> obtenerLibros() {
        return List.of();
    }
}
