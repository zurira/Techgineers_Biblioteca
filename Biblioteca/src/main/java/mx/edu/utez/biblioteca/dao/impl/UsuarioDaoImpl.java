package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Usuario;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuario {

    public List<UsuarioBiblioteca> findAll() {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT * FROM USUARIO_BIBLIOTECA";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioBiblioteca usuario = new UsuarioBiblioteca();
                usuario.setId(rs.getInt("ID"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
                usuario.setCorreo(rs.getString("CORREO"));
                usuario.setTelefono(rs.getString("TELEFONO"));
                usuario.setEstado(rs.getString("ESTADO"));
                usuario.setDireccion(rs.getString("DIRECCION"));

                usuarios.add(usuario);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }


    @Override
    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT ID FROM USUARIO_BIBLIOTECA WHERE NOMBRE = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nombre);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
