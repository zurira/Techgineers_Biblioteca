package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Libro;

import java.util.ArrayList;
import java.util.List;

public class LibroDaoImpl implements ILibro {
    @Override
    public List<Libro> obtenerLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = """
                SELECT 
                    l.ID, 
                    l.TITULO, 
                    l.ANIO_PUBLICACION,
                    l.PORTADA,
                    e.NOMBRE AS EDITORIAL,
                    (
                        SELECT LISTAGG(a.NOMBRE_COMPLETO, ', ') 
                        WITHIN GROUP (ORDER BY a.NOMBRE_COMPLETO)
                        FROM LIBRO_AUTOR la
                        JOIN AUTOR a ON la.ID_AUTOR = a.ID
                        WHERE la.ID_LIBRO = l.ID
                    ) AS AUTORES
                FROM LIBRO l
                LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID
                """;
        return List.of();
    }
}
