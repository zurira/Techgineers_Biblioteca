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
        return List.of();
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
