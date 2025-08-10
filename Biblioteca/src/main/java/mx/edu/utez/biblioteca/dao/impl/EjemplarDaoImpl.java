package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Ejemplar;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class EjemplarDaoImpl {

    public boolean insertarEjemplar(Ejemplar ejemplar, int idLibro) {
        if (existeCodigo(ejemplar.getCodigo())) {
            System.err.println("Código ya existente: " + ejemplar.getCodigo());
            return false;
        }

        String sql = "INSERT INTO EJEMPLAR (ID_LIBRO, CODIGO_LOCAL, ESTADO, UBICACION) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idLibro);
            pst.setString(2, ejemplar.getCodigo());
            pst.setString(3, ejemplar.getEstado()); // Usar el nuevo getter para el estado
            pst.setString(4, ejemplar.getUbicacion());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarVariosEjemplares(int idLibro, int cantidad, String ubicacion) {
        String sql = "INSERT INTO ejemplar (ID_LIBRO, CODIGO_LOCAL, ESTADO, UBICACION) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            // Obtener el último número de ejemplar existente y empezar a partir de ahí
            int ultimoNumero = obtenerUltimoNumeroEjemplar(idLibro);

            for (int i = 1; i <= cantidad; i++) {
                int numeroNuevo = ultimoNumero + i;
                // Genera el código local con un formato personalizado
                String codigoLocal = String.format("LIB%04d-%d", idLibro, numeroNuevo);

                ps.setInt(1, idLibro);
                ps.setString(2, codigoLocal);
                ps.setString(3, "DISPONIBLE"); // Se asigna el valor por defecto directamente
                ps.setString(4, ubicacion);
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();

            System.out.println("Se insertaron exitosamente " + cantidad + " ejemplares para el libro con ID: " + idLibro);

            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar ejemplares: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private int obtenerUltimoNumeroEjemplar(int idLibro) throws SQLException {
        String sql = "SELECT MAX(TO_NUMBER(SUBSTR(CODIGO_LOCAL, INSTR(CODIGO_LOCAL, '-') + 1))) AS ULTIMO " +
                "FROM EJEMPLAR WHERE ID_LIBRO = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ULTIMO");
            }
        }
        return 0;
    }

    private String generarCodigoEjemplar(int idLibro, int numero) {
        return String.format("LIB%04d-%03d", idLibro, numero);
    }

    public boolean existeCodigo(String codigo) {
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE CODIGO_LOCAL = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, codigo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Ejemplar> buscarEjemplaresDisponibles(String filtro) {
        ObservableList<Ejemplar> lista = FXCollections.observableArrayList();
        String sql = "SELECT e.ID, e.CODIGO_LOCAL, l.TITULO, e.UBICACION " +
                "FROM EJEMPLAR e " +
                "JOIN LIBRO l ON e.ID_LIBRO = l.ID " +
                "WHERE e.ESTADO = 'DISPONIBLE' AND (l.TITULO LIKE ? OR e.CODIGO_LOCAL LIKE ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

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
                lista.add(ejemplar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarDisponibilidad(int idEjemplar, boolean disponible) {
        String sql = "UPDATE EJEMPLAR SET ESTADO = ? WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Usar el nuevo estado, 'DISPONIBLE' o 'PRESTADO'
            String estado = disponible ? "DISPONIBLE" : "PRESTADO";
            pst.setString(1, estado);
            pst.setInt(2, idEjemplar);
            int filasActualizadas = pst.executeUpdate();
            return filasActualizadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contarEjemplaresPorLibro(int idLibro) {
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ID_LIBRO = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idLibro);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}