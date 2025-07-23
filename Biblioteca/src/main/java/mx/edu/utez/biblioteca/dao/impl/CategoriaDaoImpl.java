package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.ICategoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDaoImpl implements ICategoria {
    @Override
    public List<String> obtenerNombresCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM CATEGORIA ORDER BY NOMBRE";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categorias.add(rs.getString("NOMBRE"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categorias;

    }
    //terminado
}
