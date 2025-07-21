package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        try{
            Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Libro libro = new Libro();
                libro.setId(rs.getInt("ID"));
                libro.setTitulo(rs.getString("TITULO"));
                libro.setIdEditorial(rs.getString("EDITORIAL"));
                libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
                libro.setPortada(rs.getString("PORTADA"));
                libros.add(libro);

            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return libros;
    }
}
