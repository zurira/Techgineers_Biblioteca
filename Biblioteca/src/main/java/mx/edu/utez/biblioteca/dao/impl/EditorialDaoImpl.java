package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IEditorial;
import mx.edu.utez.biblioteca.model.Editorial;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditorialDaoImpl implements IEditorial {

    @Override
    public List<Editorial> findAll() throws Exception {
        List<Editorial> editoriales = new ArrayList<>();
        String query = "SELECT ID, NOMBRE FROM EDITORIAL";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                editoriales.add(new Editorial(rs.getInt("ID"), rs.getString("NOMBRE")));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar todas las editoriales: " + e.getMessage());
            throw e;
        }
        return editoriales;
    }

    @Override
    public Editorial findById(int id) throws Exception {
        Editorial editorial = null;
        String query = "SELECT ID, NOMBRE FROM EDITORIAL WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    editorial = new Editorial(rs.getInt("ID"), rs.getString("NOMBRE"));
                }
            } catch (SQLException e) {
                System.err.println("Error al buscar editorial por ID: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o preparación de la consulta para editorial por ID: " + e.getMessage());
            throw e;
        }
        return editorial;
    }

    @Override
    public void create(Editorial editorial) throws Exception {
        String query = "INSERT INTO EDITORIAL (NOMBRE) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, editorial.getNombre());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        editorial.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al crear editorial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Editorial editorial) throws Exception {
        String query = "UPDATE EDITORIAL SET NOMBRE = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, editorial.getNombre());
            pstmt.setInt(2, editorial.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar editorial: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM EDITORIAL WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar editorial: " + e.getMessage());
            throw e;
        }
    }
}