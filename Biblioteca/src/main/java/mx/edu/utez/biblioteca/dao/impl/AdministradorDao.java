package mx.edu.utez.biblioteca.dao.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Administrador;

import java.sql.*;

public class AdministradorDao {

    public static void cargarAdministradores(TableView<Administrador> tabla) {
        tabla.setItems(getListaAdmins(""));
    }

    public static void buscarAdministrador(TableView<Administrador> tabla, String filtro) {
        tabla.setItems(getListaAdmins(filtro));
    }

    private static ObservableList<Administrador> getListaAdmins(String filtro) {
        ObservableList<Administrador> lista = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ID, NOMBRE_COMPLETO, USUARIO, CORREO, ESTADO FROM ADMINISTRADORES WHERE LOWER(NOMBRE_COMPLETO) LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + filtro.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Administrador(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE_COMPLETO"),
                        rs.getString("USUARIO"),
                        rs.getString("CORREO"),
                        rs.getBoolean("ESTADO")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}