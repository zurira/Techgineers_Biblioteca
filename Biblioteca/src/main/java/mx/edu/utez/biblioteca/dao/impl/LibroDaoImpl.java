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

    /**
     * Extrae un objeto Libro y sus dependencias (Autor, Editorial, Categoria)
     * de un ResultSet y lo retorna.
     * @param rs El ResultSet que contiene los datos del libro.
     * @return El objeto Libro.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
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
        } catch (SQLException e) {
            System.err.println("Error al buscar todos los libros: " + e.getMessage());
            throw e;
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
            } catch (SQLException e) {
                System.err.println("Error al buscar libro por ID: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o preparación de la consulta para libro por ID: " + e.getMessage());
            throw e;
        }
        return libro;
    }

    /**
     * Nuevo método para buscar un libro por su ISBN.
     * @param isbn El ISBN del libro a buscar.
     * @return El objeto Libro si se encuentra, o null si no.
     * @throws Exception Si ocurre un error de SQL.
     */
    @Override
    public Libro findByIsbn(String isbn) throws Exception {
        Libro libro = null;
        String query = "SELECT " +
                "l.ID AS ID, l.TITULO, l.ISBN, l.SINOPSIS, l.ANIO_PUBLICACION, l.PORTADA, l.ESTADO, " +
                "e.ID AS ID, e.NOMBRE AS EDITORIAL_NOMBRE, " +
                "a.ID AS ID, a.NOMBRE_COMPLETO AS AUTOR_NOMBRE, " +
                "c.ID AS ID, c.NOMBRE AS CATEGORIA_NOMBRE " +
                "FROM LIBRO l " +
                "LEFT JOIN EDITORIAL e ON l.ID = e.ID " +
                "LEFT JOIN AUTOR a ON l.ID = a.ID " +
                "LEFT JOIN CATEGORIA c ON l.ID = c.ID " +
                "WHERE l.ISBN = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    libro = extractLibroFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar libro por ISBN: " + e.getMessage());
            throw e;
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

            // Manejo de portada: si es null, se guarda como NULL en la BD
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                pstmt.setString(5, libro.getPortada());
            } else {
                pstmt.setNull(5, Types.VARCHAR); // O el tipo SQL adecuado para tu columna
            }

            // Manejo de IDs de objetos relacionados (Autor, Editorial, Categoria)
            pstmt.setObject(6, libro.getEditorial() != null ? libro.getEditorial().getId() : null);
            pstmt.setObject(7, libro.getAutor() != null ? libro.getAutor().getId() : null);
            pstmt.setObject(8, libro.getCategoria() != null ? libro.getCategoria().getId() : null);
            pstmt.setString(9, libro.getEstado());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al crear libro: " + e.getMessage());
            throw e;
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

            // Manejo de portada en update
            if (libro.getPortada() != null && !libro.getPortada().isEmpty()) {
                pstmt.setString(5, libro.getPortada());
            } else {
                pstmt.setNull(5, Types.VARCHAR); // O el tipo SQL adecuado
            }

            pstmt.setObject(6, libro.getEditorial() != null ? libro.getEditorial().getId() : null);
            pstmt.setObject(7, libro.getAutor() != null ? libro.getAutor().getId() : null);
            pstmt.setObject(8, libro.getCategoria() != null ? libro.getCategoria().getId() : null);
            pstmt.setString(9, libro.getEstado());
            pstmt.setInt(10, libro.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM LIBRO WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            throw e;
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
        } catch (SQLException e) {
            System.err.println("Error al buscar libros: " + e.getMessage());
            throw e;
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
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del libro: " + e.getMessage());
            throw e;
        }
    }
}
