package mx.edu.utez.biblioteca.dao.impl;


import mx.edu.utez.biblioteca.model.Administrador;
import mx.edu.utez.biblioteca.config.DBConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDao {

    public static List<Administrador> buscarAdministrador(String filtro) {
        List<Administrador> lista = new ArrayList<>();
        String sql = "SELECT * FROM administradores WHERE LOWER(nombre) LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + filtro.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Administrador admin = new Administrador(
                        rs.getInt("id_admin"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("correo"),
                        rs.getString("telefono"),
                        rs.getString("contrasena"),
                        rs.getString("rol"),
                        rs.getString("direccion"),
                        rs.getString("imagen"),
                        rs.getBoolean("estado")
                );
                lista.add(admin);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

 
