package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IUsuarioBiblioteca;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioBibliotecaDaoImpl implements IUsuarioBiblioteca {

    private UsuarioBiblioteca buildUsuarioFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID");
        String nombre = rs.getString("NOMBRE");
        Date fechaNacimientoSql = rs.getDate("FECHA_NACIMIENTO");
        LocalDate fechaNacimiento = (fechaNacimientoSql != null) ? fechaNacimientoSql.toLocalDate() : null;
        String correo = rs.getString("CORREO");
        String telefono = rs.getString("TELEFONO");
        String direccion = rs.getString("DIRECCION");
        String estado = rs.getString("ESTADO");

        return new UsuarioBiblioteca(id, nombre, fechaNacimiento, correo, telefono, direccion, estado);
    }

    @Override
    public List<UsuarioBiblioteca> findAll() throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(buildUsuarioFromResultSet(rs));
            }
        }
        return usuarios;
    }

    @Override
    public UsuarioBiblioteca findById(int id) throws Exception {
        UsuarioBiblioteca usuario = null;
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = buildUsuarioFromResultSet(rs);
                }
            }
        }
        return usuario;
    }

    @Override
    public void create(UsuarioBiblioteca usuario) throws Exception {
        String query = "INSERT INTO USUARIO_BIBLIOTECA (NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1)); // Asigna el ID generado al objeto UsuarioBiblioteca
                }
            }
        }
    }

    @Override
    public void update(UsuarioBiblioteca usuario) throws Exception {
        String query = "UPDATE USUARIO_BIBLIOTECA SET NOMBRE = ?, FECHA_NACIMIENTO = ?, CORREO = ?, TELEFONO = ?, DIRECCION = ?, ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.setInt(7, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<UsuarioBiblioteca> search(String searchTerm) throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA " +
                "WHERE LOWER(NOMBRE) LIKE ? OR LOWER(CORREO) LIKE ? OR LOWER(TELEFONO) LIKE ? OR LOWER(DIRECCION) LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(buildUsuarioFromResultSet(rs));
                }
            }
        }
        return usuarios;
    }
}