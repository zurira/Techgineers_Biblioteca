package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Rol;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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