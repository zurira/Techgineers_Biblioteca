package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Editorial;
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
        String sql = "SELECT \n" +
                "    l.ID, \n" +
                "    l.TITULO, \n" +
                "    l.ANIO_PUBLICACION,\n" +
                "    l.PORTADA,\n" +
                "    l.RESUMEN,\n" +
                "    e.ID AS ID_EDITORIAL,\n" +
                "    e.NOMBRE AS NOMBRE_EDITORIAL,\n" +
                "    (\n" +
                "        SELECT LISTAGG(a.NOMBRE_COMPLETO, ', ') \n" +
                "        WITHIN GROUP (ORDER BY a.NOMBRE_COMPLETO)\n" +
                "        FROM LIBRO_AUTOR la\n" +
                "        JOIN AUTOR a ON la.ID_AUTOR = a.ID\n" +
                "        WHERE la.ID_LIBRO = l.ID\n" +
                "    ) AS AUTORES\n" +
                "FROM LIBRO l\n" +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID";
        try{
            Connection con= DBConnection.getConnection();
            PreparedStatement ps=con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Libro libro = new Libro();
                libro.setId(rs.getInt("ID"));
                libro.setTitulo(rs.getString("TITULO"));
                libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
                libro.setPortada(rs.getString("PORTADA"));
                libro.setResumen(rs.getString("RESUMEN"));

                // Cargar editorial
                Editorial editorial = new Editorial();
                editorial.setId(rs.getInt("ID_EDITORIAL"));
                editorial.setNombre(rs.getString("NOMBRE_EDITORIAL"));
                libro.setEditorial(editorial);

                // Cargar autor como texto (si no necesitas objetos Autor aún)
                Autor autor = new Autor();
                autor.setNombreCompleto(rs.getString("AUTORES")); // concatenado
                libro.setAutor(autor);

                libros.add(libro);

            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return libros;
    }
}
