package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario validateUser(String username, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.out.println("No hay conexión con la base de datos");
            return null;
        }

        try {
            String sql = "SELECT ID, USERNAME, USERPASSWORD FROM USUARIOS WHERE USERNAME=? AND USERPASSWORD=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username.trim().toLowerCase());
            stmt.setString(2, password.trim());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("userpassword")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



}

