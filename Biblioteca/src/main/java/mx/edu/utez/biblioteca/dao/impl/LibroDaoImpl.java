package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ILibro;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.config.DBConnection; // Asegúrate de que esta clase exista y funcione

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//El dao del libro
public class LibroDaoImpl implements ILibro {

    // Método auxiliar para construir un objeto Libro desde un ResultSet
    private Libro extractLibroFromResultSet(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("ID")); // Mapea ID de DB a id en modelo
        libro.setTitulo(rs.getString("TITULO"));
        libro.setIsbn(rs.getString("ISBN"));
        libro.setResumen(rs.getString("SINOPSIS")); // Mapea SINOPSIS de DB a resumen en modelo
        libro.setAnioPublicacion(rs.getInt("ANIO_PUBLICACION"));
        libro.setPortada(rs.getString("PORTADA"));

        // Recuperar Editorial
        int idEditorial = rs.getInt("ID_EDITORIAL");
        if (idEditorial != 0) {
            String nombreEditorial = rs.getString("NOMBRE_EDITORIAL");
            libro.setEditorial(new Editorial(idEditorial, nombreEditorial));
        }

        // Recuperar Categoria
        int idCategoria = rs.getInt("ID_CATEGORIA");
        if (idCategoria != 0) {
            String nombreCategoria = rs.getString("NOMBRE_CATEGORIA");
            Categoria categoria = new Categoria();
            categoria.setId(idCategoria);
            categoria.setNombre(nombreCategoria);
            libro.setCategoria(categoria);
        }

        // Recuperar Autor (principal, si ID_AUTOR en LIBRO no es nulo)
        int idAutorPrincipal = rs.getInt("ID_AUTOR");
        if (idAutorPrincipal != 0) {
            String nombreAutorPrincipal = rs.getString("NOMBRE_COMPLETO_AUTOR");
            Autor autorPrincipal = new Autor();
            autorPrincipal.setId(idAutorPrincipal);
            autorPrincipal.setNombreCompleto(nombreAutorPrincipal);
            libro.setAutor(autorPrincipal); // Asigna al campo singular 'autor'
        }

        // Recuperar Estado
        libro.setEstado(rs.getString("ESTADO"));

        return libro;
    }


    @Override
    public List<Libro> findAll() throws Exception {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT " +
                "l.ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "a.ID AS ID_AUTOR, a.NOMBRE_COMPLETO AS NOMBRE_COMPLETO_AUTOR, " +
                "c.ID AS ID_CATEGORIA, c.NOMBRE AS NOMBRE_CATEGORIA " +
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
                "l.ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "a.ID AS ID_AUTOR, a.NOMBRE_COMPLETO AS NOMBRE_COMPLETO_AUTOR, " +
                "c.ID AS ID_CATEGORIA, c.NOMBRE AS NOMBRE_CATEGORIA " +
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
                "l.ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS ID_EDITORIAL, e.NOMBRE AS NOMBRE_EDITORIAL, " +
                "a.ID AS ID_AUTOR, a.NOMBRE_COMPLETO AS NOMBRE_COMPLETO_AUTOR, " +
                "c.ID AS ID_CATEGORIA, c.NOMBRE AS NOMBRE_CATEGORIA " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID_EDITORIAL = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID_AUTOR = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID_CATEGORIA = c.ID " +
                "WHERE LOWER(l.TITULO) LIKE ? OR " +
                "LOWER(l.ISBN) LIKE ? OR " +
                "LOWER(a.NOMBRE_COMPLETO) LIKE ? OR " +
                "LOWER(e.NOMBRE) LIKE ? OR " +
                "LOWER(c.NOMBRE) LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);

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
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}