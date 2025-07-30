package mx.edu.utez.biblioteca.dao.impl;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Administrador;

import java.io.InputStream;
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
            String sql = """
                SELECT ID, NOMBRE, USERNAME, CORREO, ESTADO
                FROM USUARIO_SISTEMA
                WHERE LOWER(NOMBRE) LIKE ?
                AND ID_ROL = (SELECT ID FROM ROL WHERE NOMBRE = 'Administrador')
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + filtro.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Administrador(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("USERNAME"),
                        rs.getString("CORREO"),
                        rs.getString("ESTADO").equals("S")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Agrego esto nuevo

    public static boolean insertarAdministrador(
            String nombre, String usuario, String correo, String telefono,
            String contrasena, InputStream foto, int idRol, String estado, String direccion
    ) {
        String sql = "INSERT INTO USUARIO_SISTEMA (NOMBRE, USERNAME, CORREO, TELEFONO, PASSWORD, FOTO, ID_ROL, ESTADO, DIRECCION) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, usuario);
            stmt.setString(3, correo);
            stmt.setString(4, telefono);
            stmt.setString(5, contrasena);
            stmt.setBlob(6, foto);
            stmt.setInt(7, idRol);
            stmt.setString(8, estado);
            stmt.setString(9, direccion);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}