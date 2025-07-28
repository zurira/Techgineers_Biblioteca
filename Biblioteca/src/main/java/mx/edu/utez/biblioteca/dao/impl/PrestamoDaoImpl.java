package mx.edu.utez.biblioteca.dao.impl;

<<<<<<< HEAD
import mx.edu.utez.biblioteca.dao.IPrestamo;
import mx.edu.utez.biblioteca.model.Prestamo;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDaoImpl implements IPrestamo {

    private Prestamo buildPrestamoFromResultSet(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getInt("ID_PRESTAMO"));
        prestamo.setFechaPrestamo(rs.getDate("FECHA_PRESTAMO").toLocalDate());
        prestamo.setFechaLimite(rs.getDate("FECHA_LIMITE").toLocalDate());
        Date fechaDevolucion = rs.getDate("FECHA_DEVOLUCION");
        prestamo.setFechaReal(fechaDevolucion != null ? fechaDevolucion.toLocalDate() : null);
        prestamo.setEstado(rs.getString("ESTADO"));

        // Libro
        Libro libro = new Libro();
        libro.setId(rs.getInt("ID_LIBRO"));
        libro.setTitulo(rs.getString("TITULO_LIBRO"));
        prestamo.setLibro(libro);
        // Usuario
        UsuarioBiblioteca usuario = new UsuarioBiblioteca();
        usuario.setId(rs.getInt("ID_USUARIO"));
        usuario.setNombre(rs.getString("NOMBRE_USUARIO"));
        prestamo.setUsuario(usuario);
        return prestamo;
    }

    @Override
    public List<Prestamo> findAll() throws Exception {
        List<Prestamo> prestamos = new ArrayList<>();
        String query = "SELECT p.ID AS ID_PRESTAMO, p.FECHA_PRESTAMO, p.FECHA_LIMITE, p.FECHA_DEVOLUCION, p.ESTADO, " +
                "l.ID AS ID_LIBRO, l.TITULO AS TITULO_LIBRO, " +
                "ub.ID AS ID_USUARIO, ub.NOMBRE AS NOMBRE_USUARIO " +
                "FROM PRESTAMO p " +
                "JOIN USUARIO_BIBLIOTECA ub ON p.ID_USUARIO = ub.ID " +
                "JOIN DETALLE_PRESTAMO dp ON p.ID = dp.ID_PRESTAMO " +
                "JOIN EJEMPLAR e ON dp.ID_EJEMPLAR = e.ID " +
                "JOIN LIBRO l ON e.ID_LIBRO = l.ID";
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
    public void create(Prestamo prestamo) throws Exception {
        String query = "INSERT INTO PRESTAMO (ID_USUARIO, FECHA_PRESTAMO, FECHA_LIMITE, ESTADO) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getUsuario().getId());
            ps.setDate(2, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(3, Date.valueOf(prestamo.getFechaLimite()));
            ps.setString(4, prestamo.getEstado());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    prestamo.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Prestamo prestamo) throws Exception {
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
                "WHERE l.TITULO LIKE ? OR ub.NOMBRE LIKE ?";

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

}
=======
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IPrestamo;
import mx.edu.utez.biblioteca.model.Prestamo;
import java.sql.Connection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class PrestamoDaoImpl implements IPrestamo {
    @Override
    public int insertar(Prestamo prestamo) {
        String sql = "INSERT INTO PRESTAMO (ID_USUARIO, CORREO, FECHA_PRESTAMO, FECHA_LIMITE, FECHA_DEVOLUCION, ESTADO) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, new String[] {"ID"})) {

            pst.setInt(1, prestamo.getIdUsuario());
            pst.setString(2, prestamo.getCorreo());
            pst.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            pst.setDate(4, Date.valueOf(prestamo.getFechaLimite()));

            if (prestamo.getFechaDevolucion() != null) {
                pst.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                pst.setNull(5, Types.DATE);
            }

            pst.setString(6, prestamo.getEstado());

            int filas = pst.executeUpdate();

            if (filas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ← más seguro que usar "ID"
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}

>>>>>>> TTS17
