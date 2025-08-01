package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.model.Libro;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDaoImpl {

    public void insertar(Libro libro) {
        String sql = "INSERT INTO LIBRO (TITULO, ISBN, SINOPSIS, ANIO_PUBLICACION, ESTADO, PORTADA, " +
                "ID_AUTOR, ID_CATEGORIA, ID_EDITORIAL) VALUES (?, ?, ?, ?, ?, ?, " +
                "(SELECT ID FROM AUTOR WHERE NOMBRE_COMPLETO = ?), " +
                "(SELECT ID FROM CATEGORIA WHERE NOMBRE = ?), " +
                "(SELECT ID FROM EDITORIAL WHERE NOMBRE = ?))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getIsbn());
            stmt.setString(3, libro.getSinopsis());
            stmt.setInt(4, libro.getAnioPublicacion());
            stmt.setString(5, libro.getEstado());
            stmt.setString(6, libro.getPortada());
            stmt.setString(7, libro.getAutor());
            stmt.setString(8, libro.getCategoria());
            stmt.setString(9, libro.getEditorial());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> obtenerAutores() {
        return obtenerValores("SELECT NOMBRE_COMPLETO FROM AUTOR");
    }


    private List<String> obtenerValores(String query) {
        List<String> lista = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(rs.getString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<String> obtenerAutores() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT NOMBRE_COMPLETO FROM AUTOR";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("NOMBRE_COMPLETO"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<String> obtenerCategorias() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM CATEGORIA";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("NOMBRE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<String> obtenerEditoriales() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM EDITORIAL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("NOMBRE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
