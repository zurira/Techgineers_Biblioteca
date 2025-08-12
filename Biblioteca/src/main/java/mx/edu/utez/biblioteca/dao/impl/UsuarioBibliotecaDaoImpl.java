package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IUsuarioBiblioteca;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioBibliotecaDaoImpl implements IUsuarioBiblioteca {

    private UsuarioBiblioteca buildUsuarioFromResultSet(ResultSet rs) throws SQLException {
        UsuarioBiblioteca usuario = new UsuarioBiblioteca();
        usuario.setId(rs.getInt("ID"));
        usuario.setNombre(rs.getString("NOMBRE"));
        Date fechaNacimientoSql = rs.getDate("FECHA_NACIMIENTO");
        usuario.setFechaNacimiento((fechaNacimientoSql != null) ? fechaNacimientoSql.toLocalDate() : null);
        usuario.setCorreo(rs.getString("CORREO"));
        usuario.setTelefono(rs.getString("TELEFONO"));
        usuario.setDireccion(rs.getString("DIRECCION"));
        usuario.setEstado(rs.getString("ESTADO"));
        // Aquí se carga la fotografía
        usuario.setFotografia(rs.getBytes("FOTOGRAFIA"));
        return usuario;
    }

    private final String BASE_QUERY = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO, FOTOGRAFIA FROM USUARIO_BIBLIOTECA";

    @Override
    public List<UsuarioBiblioteca> findAll() throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        // Se corrigio la consulta para que incluye la foto
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(BASE_QUERY);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Se reutiliza el método que ya carga todos los campos, incluida la foto
                usuarios.add(buildUsuarioFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al obtener la lista de usuarios.", e);
        }
        return usuarios;
    }

    @Override
    public UsuarioBiblioteca findById(int id) throws Exception {
        UsuarioBiblioteca usuario = null;
        String query = BASE_QUERY + " WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = buildUsuarioFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al buscar el usuario por ID.", e);
        }
        return usuario;
    }

    @Override
    public boolean create(UsuarioBiblioteca usuario) throws Exception {
        String query = "INSERT INTO USUARIO_BIBLIOTECA (NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO, FOTOGRAFIA) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, usuario.getNombre());
            ps.setDate(2, (usuario.getFechaNacimiento() != null) ? Date.valueOf(usuario.getFechaNacimiento()) : null);
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.setBytes(7, usuario.getFotografia());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al crear el usuario.", e);
        }
    }

    @Override
    public boolean update(UsuarioBiblioteca usuario) throws Exception {
        String query = "UPDATE USUARIO_BIBLIOTECA SET NOMBRE = ?, FECHA_NACIMIENTO = ?, CORREO = ?, TELEFONO = ?, DIRECCION = ?, ESTADO = ?, FOTOGRAFIA = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, usuario.getNombre());
            ps.setDate(2, (usuario.getFechaNacimiento() != null) ? Date.valueOf(usuario.getFechaNacimiento()) : null);
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.setBytes(7, usuario.getFotografia());
            ps.setInt(8, usuario.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al actualizar el usuario.", e);
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String query = "DELETE FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al eliminar el usuario.", e);
        }
    }

    @Override
    public List<UsuarioBiblioteca> search(String searchTerm) throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = BASE_QUERY + " WHERE LOWER(NOMBRE) LIKE ? OR LOWER(CORREO) LIKE ? OR LOWER(TELEFONO) LIKE ? OR LOWER(DIRECCION) LIKE ?";
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al buscar usuarios.", e);
        }
        return usuarios;
    }

    @Override
    public boolean existeNombre(String nombre) throws Exception {
        String query = "SELECT COUNT(*) FROM USUARIO_BIBLIOTECA WHERE NOMBRE = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error al verificar si el nombre existe.", e);
        }
        return false;
    }
}