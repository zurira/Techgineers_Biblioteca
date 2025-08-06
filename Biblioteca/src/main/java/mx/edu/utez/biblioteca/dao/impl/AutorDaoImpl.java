package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IAutor;
import mx.edu.utez.biblioteca.model.Autor;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AutorDaoImpl implements IAutor {

    @Override
    public List<Autor> findAll() throws Exception {
        List<Autor> autores = new ArrayList<>();
        String query = "SELECT ID, NOMBRE_COMPLETO FROM AUTOR";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                autores.add(new Autor(rs.getInt("ID"), rs.getString("NOMBRE_COMPLETO")));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar todos los autores: " + e.getMessage());
            throw e;
        }
        return autores;
    }

    @Override
    public Autor findById(int id) throws Exception {
        Autor autor = null;
        String query = "SELECT ID, NOMBRE_COMPLETO FROM AUTOR WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    autor = new Autor(rs.getInt("ID"), rs.getString("NOMBRE_COMPLETO"));
                }
            } catch (SQLException e) {
                System.err.println("Error al buscar autor por ID: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o preparación de la consulta para autor por ID: " + e.getMessage());
            throw e;
        }
        return autor;
    }

    @Override
    public Autor findByName(String name) throws Exception {
        Autor autor = null;
        // Usar UPPER para hacer la búsqueda insensible a mayúsculas/minúsculas
        String query = "SELECT ID, NOMBRE_COMPLETO FROM AUTOR WHERE UPPER(NOMBRE_COMPLETO) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    autor = new Autor(rs.getInt("ID"), rs.getString("NOMBRE_COMPLETO"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar autor por nombre: " + e.getMessage());
            throw e;
        }
        return autor;
    }

    @Override
    public boolean create(Autor autor) throws Exception {
        // Usar Statement.RETURN_GENERATED_KEYS para columnas IDENTITY
        String query = "INSERT INTO AUTOR (NOMBRE_COMPLETO) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, autor.getNombreCompleto());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        //autor.setId(rs.getInt(1)); // Debería funcionar para IDENTITY columns
                   return true;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al crear autor: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Autor autor) throws Exception {
        String query = "UPDATE AUTOR SET NOMBRE_COMPLETO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, autor.getNombreCompleto());
            pstmt.setInt(2, autor.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar autor: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM AUTOR WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar autor: " + e.getMessage());
            throw e;
        }
    }
}