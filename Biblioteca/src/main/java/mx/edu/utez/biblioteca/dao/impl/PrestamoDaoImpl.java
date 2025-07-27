package mx.edu.utez.biblioteca.dao.impl;

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
        return null;
    }

    @Override
    public void create(Prestamo prestamo) throws Exception {

    }

    @Override
    public void update(Prestamo prestamo) throws Exception {

    }

    @Override
    public void delete(int id) throws Exception {

    }

    @Override
    public List<Prestamo> search(String searchTerm) throws Exception {
        return List.of();
    }
}
