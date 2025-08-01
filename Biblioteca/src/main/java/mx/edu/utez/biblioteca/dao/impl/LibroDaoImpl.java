package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDaoImpl implements ILibro {

    private Libro extractLibroFromResultSet(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("LIBRO_ID"));
        libro.setTitulo(rs.getString("TITULO"));
        libro.setIsbn(rs.getString("ISBN"));
        libro.setResumen(rs.getString("SINOPSIS"));
        libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
        libro.setPortada(rs.getString("PORTADA"));
        libro.setEstado(rs.getString("ESTADO"));

        int idEditorial = rs.getInt("EDITORIAL_ID");
        if (!rs.wasNull()) {
            libro.setEditorial(new Editorial(idEditorial, rs.getString("EDITORIAL_NOMBRE")));
        }

        int idAutor = rs.getInt("AUTOR_ID");
        if (!rs.wasNull()) {
            libro.setAutor(new Autor(idAutor, rs.getString("AUTOR_NOMBRE")));
        }

        int idCategoria = rs.getInt("CATEGORIA_ID");
        if (!rs.wasNull()) {
            libro.setCategoria(new Categoria(idCategoria, rs.getString("CATEGORIA_NOMBRE")));
        }

        return libro;
    }

    @Override
    public List<Libro> findAll() throws Exception {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT " +
                "l.ID AS LIBRO_ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS EDITORIAL_ID, e.NOMBRE AS EDITORIAL_NOMBRE, " +
                "a.ID AS AUTOR_ID, a.NOMBRE_COMPLETO AS AUTOR_NOMBRE, " +
                "c.ID AS CATEGORIA_ID, c.NOMBRE AS CATEGORIA_NOMBRE " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID_CATEGORIA = c.ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                libros.add(extractLibroFromResultSet(rs));
            }
        }
        return libros;
    }

    @Override
    public Libro findById(int id) throws Exception {
        Libro libro = null;
        String query = "SELECT " +
                "l.ID AS LIBRO_ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS EDITORIAL_ID, e.NOMBRE AS EDITORIAL_NOMBRE, " +
                "a.ID AS AUTOR_ID, a.NOMBRE_COMPLETO AS AUTOR_NOMBRE, " +
                "c.ID AS CATEGORIA_ID, c.NOMBRE AS CATEGORIA_NOMBRE " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID_CATEGORIA = c.ID " +
                "WHERE l.ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    libro = extractLibroFromResultSet(rs);
                }
            }
        }
        return libro;
    }

    @Override
    public void create(Libro libro) throws Exception {

        String query = "INSERT INTO LIBRO (TITULO, ISBN, SINOPSIS, ANIO_PUBLICACION, PORTADA, ID_EDITORIAL, ID_AUTOR, ID_CATEGORIA, ESTADO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getIsbn());
            pstmt.setString(3, libro.getResumen());
            pstmt.setInt(4, libro.getAnioPublicacion());
            pstmt.setString(5, libro.getPortada());
            pstmt.setObject(6, libro.getEditorial() != null ? libro.getEditorial().getId() : null);
            pstmt.setObject(7, libro.getAutor() != null ? libro.getAutor().getId() : null);
            pstmt.setObject(8, libro.getCategoria() != null ? libro.getCategoria().getId() : null);
            pstmt.setString(9, libro.getEstado());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Libro libro) throws Exception {
        String query = "UPDATE LIBRO SET TITULO = ?, ISBN = ?, SINOPSIS = ?, ANIO_PUBLICACION = ?, " +
                "PORTADA = ?, ID_EDITORIAL = ?, ID_AUTOR = ?, ID_CATEGORIA = ?, ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getIsbn());
            pstmt.setString(3, libro.getResumen());
            pstmt.setInt(4, libro.getAnioPublicacion());
            pstmt.setString(5, libro.getPortada());
            pstmt.setObject(6, libro.getEditorial() != null ? libro.getEditorial().getId() : null);
            pstmt.setObject(7, libro.getAutor() != null ? libro.getAutor().getId() : null);
            pstmt.setObject(8, libro.getCategoria() != null ? libro.getCategoria().getId() : null);
            pstmt.setString(9, libro.getEstado());
            pstmt.setInt(10, libro.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM LIBRO WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Libro> search(String searchTerm) throws Exception {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT " +
                "l.ID AS LIBRO_ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS EDITORIAL_ID, e.NOMBRE AS EDITORIAL_NOMBRE, " +
                "a.ID AS AUTOR_ID, a.NOMBRE_COMPLETO AS AUTOR_NOMBRE, " +
                "c.ID AS CATEGORIA_ID, c.NOMBRE AS CATEGORIA_NOMBRE " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID_CATEGORIA = c.ID " +
                "WHERE LOWER(l.TITULO) LIKE ? OR LOWER(l.ISBN) LIKE ? OR " +
                "LOWER(a.NOMBRE_COMPLETO) LIKE ? OR LOWER(e.NOMBRE) LIKE ? OR LOWER(c.NOMBRE) LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, pattern);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(extractLibroFromResultSet(rs));
                }
            }
        }
        return libros;
    }

    @Override
    public boolean updateStatus(int idLibro, String estado) throws Exception {
        String query = "UPDATE LIBRO SET ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, idLibro);
            return pstmt.executeUpdate() > 0;
        }
    }
}

