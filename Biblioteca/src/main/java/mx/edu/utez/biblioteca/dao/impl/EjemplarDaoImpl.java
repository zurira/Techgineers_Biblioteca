package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Ejemplar;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class EjemplarDaoImpl {

    /**
     * Busca ejemplares disponibles que coincidan con un filtro de texto.
     * El filtro puede aplicarse sobre el título o el código local.
     */
    public ObservableList<Ejemplar> buscarEjemplaresDisponibles(String filtro) {
        ObservableList<Ejemplar> lista = FXCollections.observableArrayList();
        String sql = "SELECT e.ID, e.CODIGO_LOCAL, l.TITULO, e.UBICACION " +
                "FROM EJEMPLAR e " +
                "JOIN LIBRO l ON e.ID_LIBRO = l.ID " +
                "WHERE e.DISPONIBLE = 'S' AND (l.TITULO LIKE ? OR e.CODIGO_LOCAL LIKE ?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);

            String query = "%" + filtro + "%";
            pst.setString(1, query);
            pst.setString(2, query);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Ejemplar ejemplar = new Ejemplar();

                ejemplar.setIdEjemplar(rs.getInt("ID"));
                ejemplar.setCodigo(rs.getString("CODIGO_LOCAL"));
                ejemplar.setTitulo(rs.getString("TITULO"));
                ejemplar.setUbicacion(rs.getString("UBICACION"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    /**
     * Actualiza el estado de disponibilidad de un ejemplar físico.
     * Se usará después de prestarlo o devolverlo.
     */
    public void actualizarDisponibilidad(int idEjemplar, boolean disponible) {
        String sql = "UPDATE EJEMPLAR SET DISPONIBLE = ? WHERE ID = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, disponible ? "S" : "N");
            pst.setInt(2, idEjemplar);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}