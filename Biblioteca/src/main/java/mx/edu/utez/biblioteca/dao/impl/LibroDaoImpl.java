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
        String sql = "SELECT " +
                "l.ID, l.TITULO, l.ANIO_PUBLICACION, l.PORTADA, l.SINOPSIS, l.ISBN, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "a.ID AS ID_AUTOR, a.NOMBRE_COMPLETO AS AUTORES " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("ID"));
                libro.setTitulo(rs.getString("TITULO"));
                libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
                libro.setPortada(rs.getString("PORTADA"));
                libro.setResumen(rs.getString("SINOPSIS"));
                libro.setIsbn(rs.getString("ISBN"));

                Editorial editorial = new Editorial();
                editorial.setId(rs.getInt("ID_EDITORIAL"));
                editorial.setNombre(rs.getString("NOMBRE_EDITORIAL"));
                libro.setEditorial(editorial);

                Autor autor = new Autor();
                autor.setId(rs.getInt("ID_AUTOR"));
                autor.setNombreCompleto(rs.getString("AUTORES"));
                libro.setAutor(autor);

                libros.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return libros;
    }

    public List<Libro> obtenerLibrosPorFiltro(String filtro, String categoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT l.ID, l.TITULO, l.ANIO_PUBLICACION, l.PORTADA, l.SINOPSIS, l.ISBN, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "a.ID AS ID_AUTOR, a.NOMBRE_COMPLETO AS AUTORES " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID_CATEGORIA = c.ID " +
                "WHERE (LOWER(l.TITULO) LIKE ? OR LOWER(l.ISBN) LIKE ? OR LOWER(a.NOMBRE_COMPLETO) LIKE ?)";

        if (categoria != null && !categoria.isBlank()) {
            sql += " AND LOWER(c.NOMBRE) = ?";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
                libro.setResumen(rs.getString("SINOPSIS"));
                libro.setIsbn(rs.getString("ISBN"));

                Editorial editorial = new Editorial();
                editorial.setId(rs.getInt("ID_EDITORIAL"));
                editorial.setNombre(rs.getString("NOMBRE_EDITORIAL"));
                libro.setEditorial(editorial);

                Autor autor = new Autor();
                autor.setId(rs.getInt("ID_AUTOR"));
                autor.setNombreCompleto(rs.getString("AUTORES"));
                libro.setAutor(autor);

                libros.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return libros;
    }
}
