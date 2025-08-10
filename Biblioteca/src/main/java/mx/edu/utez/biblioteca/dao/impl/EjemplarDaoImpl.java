package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Ejemplar;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class EjemplarDaoImpl {

    public List<Ejemplar> findByLibro(int idLibro) throws SQLException {
        List<Ejemplar> ejemplares = new ArrayList<>();
        String sql = "SELECT ID, ID_LIBRO, CODIGO_LOCAL, ESTADO, UBICACION FROM EJEMPLAR WHERE ID_LIBRO = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ejemplar ejemplar = new Ejemplar();
                    ejemplar.setIdEjemplar(rs.getInt("ID"));
                    // Ahora se asigna el ID del libro al objeto ejemplar que se está creando
                    ejemplar.setIdLibro(rs.getInt("ID_LIBRO"));
                    ejemplar.setCodigo(rs.getString("CODIGO_LOCAL"));
                    ejemplar.setEstado(rs.getString("ESTADO"));
                    ejemplar.setUbicacion(rs.getString("UBICACION"));
                    ejemplares.add(ejemplar);
                }
            }
        }
        return ejemplares;
    }

    public boolean insertarVariosEjemplares(int idLibro, int cantidad, String ubicacion) {
        String sql = "INSERT INTO EJEMPLAR (ID_LIBRO, CODIGO_LOCAL, ESTADO, UBICACION) VALUES (?, ?, ?, ?)";
        Connection conn = null; // Declarar la conexión fuera del try-with-resources para el rollback
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            conn.setAutoCommit(false);
            int ultimoNumero = obtenerUltimoNumeroEjemplar(idLibro);

            // Verifica si ya existe un ejemplar de reserva para este libro
            boolean existeReserva = existeEjemplarReserva(idLibro);

            for (int i = 1; i <= cantidad; i++) {
                int numeroNuevo = ultimoNumero + i;
                String codigoLocal = String.format("LIB%04d-%d", idLibro, numeroNuevo);

                ps.setInt(1, idLibro);
                ps.setString(2, codigoLocal);
                ps.setString(4, ubicacion);

                // Si no hay reserva y es el primer ejemplar a insertar, lo marcamos como reserva
                if (!existeReserva && i == 1) {
                    ps.setString(3, "RESERVA");
                    existeReserva = true; // Ya no insertaremos más reservas
                } else {
                    ps.setString(3, "DISPONIBLE");
                }
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            System.out.println("Se insertaron exitosamente " + cantidad + " ejemplares para el libro con ID: " + idLibro);
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar ejemplares: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            // Asegúrate de cerrar la conexión si es necesario
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int obtenerUltimoNumeroEjemplar(int idLibro) throws SQLException {
        String sql = "SELECT MAX(TO_NUMBER(SUBSTR(CODIGO_LOCAL, INSTR(CODIGO_LOCAL, '-') + 1))) AS ULTIMO " +
                "FROM EJEMPLAR WHERE ID_LIBRO = ? " +
                "AND INSTR(CODIGO_LOCAL, '-') > 0 AND REGEXP_LIKE(SUBSTR(CODIGO_LOCAL, INSTR(CODIGO_LOCAL, '-') + 1), '^[0-9]+$')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { // Se prepara la consulta
            ps.setInt(1, idLibro); // Se asigna el ID del libro aquí

            try (ResultSet rs = ps.executeQuery()) { // Se ejecuta la consulta después de asignar el ID
                if (rs.next()) {
                    return rs.getInt("ULTIMO");
                }
            }
        }
        return 0;
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

    public Map<Integer, Integer> obtenerStockPorLibro() throws SQLException {
        Map<Integer, Integer> stockPorLibro = new HashMap<>();
        String sql = "SELECT ID_LIBRO, COUNT(*) AS stock_disponible " +
                "FROM EJEMPLAR " +
                "WHERE ESTADO = 'DISPONIBLE' " +
                "GROUP BY ID_LIBRO";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idLibro = rs.getInt("ID_LIBRO");
                int stock = rs.getInt("stock_disponible");
                stockPorLibro.put(idLibro, stock);
            }
        }
        return stockPorLibro;
    }

    // Método para verificar si ya existe una reserva de ejemplar
    private boolean existeEjemplarReserva(int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ID_LIBRO = ? AND ESTADO = 'RESERVA'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}