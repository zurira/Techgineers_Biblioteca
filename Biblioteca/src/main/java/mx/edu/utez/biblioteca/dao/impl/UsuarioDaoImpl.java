package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Rol;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuario {

    @Override
    public Usuario login(String input, String pass) throws Exception {
        Usuario usuario = null;
        String sql = "SELECT u.ID, u.CORREO, u.USERNAME, u.PASSWORD, r.ID AS ID_ROL, r.NOMBRE AS NOMBRE_ROL " +
                "FROM USUARIO_SISTEMA u " +
                "JOIN ROL r ON u.ID_ROL = r.ID " +
                "WHERE (u.CORREO = ? OR u.USERNAME = ?) AND u.PASSWORD = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, input);
            ps.setString(2, input);
            ps.setString(3, pass);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                usuario = new Usuario();
                usuario.setId(resultSet.getInt("ID"));
                usuario.setCorreo(resultSet.getString("CORREO"));
                usuario.setPassword(resultSet.getString("PASSWORD"));
                usuario.setUsername(resultSet.getString("USERNAME"));

                Rol rol = new Rol();
                rol.setId(resultSet.getInt("ID_ROL"));
                rol.setNombre(resultSet.getString("NOMBRE_ROL"));
                usuario.setRol(rol);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        return usuario;
    }

    @Override
    public List<Usuario> findAll() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String query = "SELECT u.*, r.ID AS ROL_ID, r.NOMBRE AS ROL_NOMBRE FROM USUARIO_SISTEMA u JOIN ROL r ON u.ID_ROL = r.ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("ROL_ID"), rs.getString("ROL_NOMBRE"));
                Usuario usuario = new Usuario(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("CORREO"),
                        rs.getString("USERNAME"),
                        rs.getString("PASSWORD"),
                        rol,
                        rs.getString("DIRECCION"),
                        rs.getString("ESTADO"),
                        rs.getBytes("FOTO")
                );
                lista.add(usuario);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener usuarios: " + e.getMessage(), e);
        }
        return lista;
    }



    @Override
    public void create(Usuario usuario) throws Exception {
        String query = "INSERT INTO USUARIO_SISTEMA (NOMBRE, CORREO, TELEFONO, USERNAME, PASSWORD, FOTO, ID_ROL, ESTADO, DIRECCION) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getPassword());
            ps.setBytes(6, usuario.getFoto());
            ps.setInt(7, usuario.getRol().getId());
            ps.setString(8, usuario.getEstado());
            ps.setString(9, usuario.getDireccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario findById(int id) throws Exception {
        Usuario usuario = null;
        String query = "SELECT u.*, r.ID AS ROL_ID, r.NOMBRE AS ROL_NOMBRE " +
                "FROM USUARIO_SISTEMA u " +
                "JOIN ROL r ON u.ID_ROL = r.ID " +
                "WHERE u.ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Rol rol = new Rol(rs.getInt("ROL_ID"), rs.getString("ROL_NOMBRE"));

                usuario = new Usuario(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("CORREO"),
                        rs.getString("USERNAME"),
                        rs.getString("PASSWORD"),
                        rol,
                        rs.getString("DIRECCION"),
                        rs.getString("ESTADO"),
                        rs.getBytes("FOTO")
                );
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por ID: " + e.getMessage(), e);
        }
        return usuario;
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        String query = "UPDATE USUARIO_SISTEMA SET NOMBRE = ?, CORREO = ?, TELEFONO = ?, USERNAME = ?, PASSWORD = ?, FOTO = ?, ID_ROL = ?, ESTADO = ?, DIRECCION = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getTelefono());
            ps.setString(4, usuario.getUsername());
            ps.setString(5, usuario.getPassword());
            ps.setBytes(6, usuario.getFoto());
            ps.setInt(7, usuario.getRol().getId());
            ps.setString(8, usuario.getEstado());
            ps.setString(9, usuario.getDireccion());
            ps.setInt(10, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Usuario> findByRolNombre(String nombreRol) throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String query = "SELECT u.*, r.ID AS ROL_ID, r.NOMBRE AS ROL_NOMBRE " +
                "FROM USUARIO_SISTEMA u " +
                "JOIN ROL r ON u.ID_ROL = r.ID " +
                "WHERE r.NOMBRE = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombreRol);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rol rol = new Rol(rs.getInt("ROL_ID"), rs.getString("ROL_NOMBRE"));
                Usuario usuario = new Usuario(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("CORREO"),
                        rs.getString("USERNAME"),
                        rs.getString("PASSWORD"),
                        rol,
                        rs.getString("DIRECCION"),
                        rs.getString("ESTADO"),
                        rs.getBytes("FOTO")
                );
                lista.add(usuario);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener usuarios por rol: " + e.getMessage(), e);
        }
        return lista;
    }




    public static void main(String[] args) {
        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            System.out.println(dao.login("",""));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}




