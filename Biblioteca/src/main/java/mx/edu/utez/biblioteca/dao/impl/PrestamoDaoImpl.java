package mx.edu.utez.biblioteca.dao.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mx.edu.utez.biblioteca.dao.IPrestamo;
import mx.edu.utez.biblioteca.model.Ejemplar;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDaoImpl implements IPrestamo {
// PrestamoDaoImpl.java

// PrestamoDaoImpl.java

    private Prestamo buildPrestamoFromResultSet(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("ID_PRESTAMO"));
        prestamo.setFechaPrestamo(rs.getDate("FECHA_PRESTAMO").toLocalDate());
        prestamo.setFechaLimite(rs.getDate("FECHA_LIMITE").toLocalDate());
        Date fechaDevolucion = rs.getDate("FECHA_DEVOLUCION");
        prestamo.setFechaReal(fechaDevolucion != null ? fechaDevolucion.toLocalDate() : null);
        prestamo.setEstado(rs.getString("ESTADO"));

        // ✅ Se crea y asigna el objeto Usuario
        UsuarioBiblioteca usuario = new UsuarioBiblioteca();
        usuario.setId(rs.getInt("ID_USUARIO"));
        usuario.setNombre(rs.getString("NOMBRE_USUARIO"));
        prestamo.setUsuario(usuario);

        // ✅ Se crea y asigna el objeto Libro
        Libro libro = new Libro();
        libro.setId(rs.getInt("ID_LIBRO"));
        libro.setTitulo(rs.getString("TITULO_LIBRO"));

        // ✅ Se crea y asigna el objeto Ejemplar, y se le asigna el Libro
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setIdEjemplar(rs.getInt("ID_EJEMPLAR"));
        ejemplar.setCodigo(rs.getString("CODIGO_EJEMPLAR"));
        ejemplar.setUbicacion(rs.getString("UBICACION_EJEMPLAR"));
        ejemplar.setLibro(libro); // Se establece la relación entre Ejemplar y Libro

        // ✅ Se asigna el Ejemplar al Prestamo
        prestamo.setEjemplar(ejemplar);

        return prestamo;
    }

    // PrestamoDaoImpl.java

    @Override
    public List<Prestamo> findAll() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String query = "SELECT DISTINCT " +
                "p.ID AS ID_PRESTAMO, p.FECHA_PRESTAMO, p.FECHA_LIMITE, p.FECHA_DEVOLUCION, p.ESTADO, " +
                "ub.ID AS ID_USUARIO, ub.NOMBRE AS NOMBRE_USUARIO, " +
                "e.ID AS ID_EJEMPLAR, e.CODIGO_LOCAL AS CODIGO_EJEMPLAR, e.UBICACION AS UBICACION_EJEMPLAR, " + // ✅ AQUÍ ESTÁ LA CORRECCIÓN
                "l.ID AS ID_LIBRO, l.TITULO AS TITULO_LIBRO " +
                "FROM PRESTAMO p " +
                "LEFT JOIN USUARIO_BIBLIOTECA ub ON p.ID_USUARIO = ub.ID " +
                "LEFT JOIN EJEMPLAR e ON p.ID_EJEMPLAR = e.ID " +
                "LEFT JOIN LIBRO l ON e.ID_LIBRO = l.ID " +
                "ORDER BY p.ID ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prestamos.add(buildPrestamoFromResultSet(rs));
            }
        }
        return prestamos;
    }

    @Override
    public Prestamo findById(int id) throws Exception {
        Prestamo prestamo = null;
        String query = "SELECT p.ID AS ID_PRESTAMO, p.FECHA_PRESTAMO, p.FECHA_LIMITE, p.FECHA_DEVOLUCION, p.ESTADO, " +
                "l.ID AS ID_LIBRO, l.TITULO AS TITULO_LIBRO, " +
                "ub.ID AS ID_USUARIO, ub.NOMBRE AS NOMBRE_USUARIO " +
                "FROM PRESTAMO p " +
                "JOIN USUARIO_BIBLIOTECA ub ON p.ID_USUARIO = ub.ID " +
                "JOIN DETALLE_PRESTAMO dp ON p.ID = dp.ID_PRESTAMO " +
                "JOIN EJEMPLAR e ON dp.ID_EJEMPLAR = e.ID " +
                "JOIN LIBRO l ON e.ID_LIBRO = l.ID " +
                "WHERE p.ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prestamo = buildPrestamoFromResultSet(rs);
                }
            }
        }
        return prestamo;
    }

    @Override
    public boolean create(Prestamo prestamo) throws Exception {
        String query = "INSERT INTO PRESTAMO (ID_USUARIO, FECHA_PRESTAMO, FECHA_LIMITE, ESTADO, ID_EJEMPLAR) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, new String[]{"ID"})) {

            // ✅ Se corrigen los índices de los parámetros para que coincidan con la consulta SQL.
            ps.setInt(1, prestamo.getUsuario().getId());
            ps.setDate(2, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(3, Date.valueOf(prestamo.getFechaLimite()));
            ps.setString(4, prestamo.getEstado());
            ps.setInt(5, prestamo.getEjemplar().getIdEjemplar());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                    System.out.println("ID del préstamo generado: " + prestamo.getId());
                }
            }

            return true;
        }
    }


    @Override
    public boolean update(Prestamo prestamo) throws Exception {
        String query = "UPDATE PRESTAMO SET ID_USUARIO = ?, FECHA_PRESTAMO = ?, FECHA_LIMITE = ?, FECHA_DEVOLUCION = ?, ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, prestamo.getUsuario().getId());
            ps.setDate(2, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(3, Date.valueOf(prestamo.getFechaLimite()));
            ps.setDate(4, prestamo.getFechaReal() != null ? Date.valueOf(prestamo.getFechaReal()) : null);
            ps.setString(5, prestamo.getEstado());
            ps.setInt(6, prestamo.getId());
            ps.executeUpdate();
        }
        return true;
    }

    @Override
    public List<Prestamo> search(String searchTerm) throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String query = "SELECT p.ID AS ID_PRESTAMO, p.FECHA_PRESTAMO, p.FECHA_LIMITE, p.FECHA_DEVOLUCION, p.ESTADO, " +
                "l.ID AS ID_LIBRO, l.TITULO AS TITULO_LIBRO, " +
                "ub.ID AS ID_USUARIO, ub.NOMBRE AS NOMBRE_USUARIO " +
                "FROM PRESTAMO p " +
                "JOIN USUARIO_BIBLIOTECA ub ON p.ID_USUARIO = ub.ID " +
                "JOIN DETALLE_PRESTAMO dp ON p.ID = dp.ID_PRESTAMO " +
                "JOIN EJEMPLAR e ON dp.ID_EJEMPLAR = e.ID " +
                "JOIN LIBRO l ON e.ID_LIBRO = l.ID " +
                "WHERE l.TITULO LIKE ? OR ub.NOMBRE LIKE ? " +
                "ORDER BY p.ID ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(buildPrestamoFromResultSet(rs));
                }
            }
        }
        return prestamos;
    }

    @Override
    public ObservableList<UsuarioBiblioteca> obtenerUsuarios() {
        ObservableList<UsuarioBiblioteca> usuarios = FXCollections.observableArrayList();
        String sql = "SELECT ID, NOMBRE FROM USUARIO_BIBLIOTECA";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UsuarioBiblioteca usuario = new UsuarioBiblioteca();
                usuario.setId(rs.getInt("ID"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuarios.add(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}