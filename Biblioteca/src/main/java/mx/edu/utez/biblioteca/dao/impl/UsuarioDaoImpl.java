package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuario {

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



    @Override
    public List<String> obtenerTodosLosNombres() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM USUARIO_BIBLIOTECA";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("NOMBRE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombres;
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

