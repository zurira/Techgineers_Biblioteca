package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.*;

public class ConfiguracionDaoImpl {
    private Connection conn;

    public ConfiguracionDaoImpl() {
        try {
            this.conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("Conexión inválida o cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error en el constructor: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public double obtenerTarifaMulta() throws SQLException {
        String sql = "SELECT VALOR FROM configuracion";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("VALOR");
            } else {
                throw new SQLException("No se encontró la tarifa de multa en la tabla.");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la tarifa: " + e.getMessage());
            throw e; // repropagamos para saber dónde explotó
        }
    }

    public void actualizarTarifaMulta(double tarifa) throws SQLException {
        String sql = "UPDATE CONFIGURACION SET VALOR = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, tarifa);
            int filas = stmt.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se actualizó ninguna fila. ¿Existe el registro?");
            }
        }
    }
}
