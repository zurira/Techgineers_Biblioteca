package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.ICategoria;
import mx.edu.utez.biblioteca.model.Categoria;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDaoImpl implements ICategoria {

    @Override
    public List<Categoria> findAll() throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        String query = "SELECT ID, NOMBRE FROM CATEGORIA";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(new Categoria(rs.getInt("ID"), rs.getString("NOMBRE")));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar todas las categorías: " + e.getMessage());
            throw e;
        }
        return categorias;
    }

    @Override
    public Categoria findById(int id) throws Exception {
        Categoria categoria = null;
        String query = "SELECT ID, NOMBRE FROM CATEGORIA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(rs.getInt("ID"), rs.getString("NOMBRE"));
                }
            } catch (SQLException e) {
                System.err.println("Error al buscar categoría por ID: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o preparación de la consulta para categoría por ID: " + e.getMessage());
            throw e;
        }
        return categoria;
    }

    @Override
    public Categoria findByName(String name) throws Exception {
        Categoria categoria = null;
        // Usar UPPER para hacer la búsqueda insensible a mayúsculas/minúsculas
        String query = "SELECT ID, NOMBRE FROM CATEGORIA WHERE UPPER(NOMBRE) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categoria(rs.getInt("ID"), rs.getString("NOMBRE"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categoría por nombre: " + e.getMessage());
            throw e;
        }
        return categoria;
    }

    @Override
    public boolean create(Categoria categoria) throws Exception {
        // Usar Statement.RETURN_GENERATED_KEYS para columnas IDENTITY
        String query = "INSERT INTO CATEGORIA (NOMBRE) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, categoria.getNombre());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        //categoria.setId(rs.getInt(1)); // Debería funcionar para IDENTITY columns
                        return true;
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Categoria categoria) throws Exception {
        String query = "UPDATE CATEGORIA SET NOMBRE = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, categoria.getNombre());
            pstmt.setInt(2, categoria.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM CATEGORIA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            throw e;
        }
    }

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
}
