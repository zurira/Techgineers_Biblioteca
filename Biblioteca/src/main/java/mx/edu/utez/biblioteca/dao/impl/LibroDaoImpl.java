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

                // Cargar datos de la editorial
                Editorial editorial = new Editorial();
                editorial.setId(rs.getInt("ID_EDITORIAL"));
                editorial.setNombre(rs.getString("NOMBRE_EDITORIAL"));
                libro.setEditorial(editorial);

                // Cargar datos del autor
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

    public List<Libro> obtenerLibrosPorFiltro(String filtro, String categoria){
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.ID, l.TITULO, l.ANIO_PUBLICACION, l.PORTADA, l.RESUMEN, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "(SELECT LISTAGG(a.NOMBRE_COMPLETO, ', ') WITHIN GROUP (ORDER BY a.NOMBRE_COMPLETO) " +
                " FROM LIBRO_AUTOR la JOIN AUTOR a ON la.ID_AUTOR = a.ID WHERE la.ID_LIBRO = l.ID) AS AUTORES " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN LIBRO_CATEGORIA lc ON lc.ID_LIBRO = l.ID " +
                "LEFT JOIN CATEGORIA c ON lc.ID_CATEGORIA = c.ID " +
                "WHERE (LOWER(l.TITULO) LIKE ? OR LOWER(l.ISBN) LIKE ? OR EXISTS " +
                " (SELECT 1 FROM LIBRO_AUTOR la JOIN AUTOR a ON la.ID_AUTOR = a.ID " +
                "  WHERE la.ID_LIBRO = l.ID AND LOWER(a.NOMBRE_COMPLETO) LIKE ?))";

        if (categoria != null && !categoria.isBlank()) {
            sql += " AND LOWER(c.NOMBRE) = ?";
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            String filtroLike = "%" + filtro.toLowerCase() + "%";

            int index = 1;
            ps.setString(index++, filtroLike);
            ps.setString(index++, filtroLike);
            ps.setString(index++, filtroLike);

            if (categoria != null && !categoria.isBlank()) {
                ps.setString(index++, categoria.toLowerCase());
            }



            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("ID"));
                libro.setTitulo(rs.getString("TITULO"));
                libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
                libro.setPortada(rs.getString("PORTADA"));
                libro.setResumen(rs.getString("RESUMEN"));

                //Se cargan los datos de la editorial
                Editorial editorial = new Editorial();
                editorial.setId(rs.getInt("ID_EDITORIAL"));
                editorial.setNombre(rs.getString("NOMBRE_EDITORIAL"));
                libro.setEditorial(editorial);

                //Se cargan datos del autor
                Autor autor = new Autor();
                autor.setNombreCompleto(rs.getString("AUTORES"));
                libro.setAutor(autor);



            }

        }catch(Exception e) {
            e.printStackTrace();
        }


        return libros;
    }
}
