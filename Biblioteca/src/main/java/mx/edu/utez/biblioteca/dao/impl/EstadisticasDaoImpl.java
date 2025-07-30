package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IEstadisticas;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstadisticasDaoImpl implements IEstadisticas {

    @Override
    public List<Map<String, Object>> getTop5MostBorrowedBooks() {
        List<Map<String, Object>> topBooks = new ArrayList<>();
        // Consulta para obtener los 5 libros más prestados en el último mes
        String query = "SELECT L.TITULO, COUNT(DP.ID_EJEMPLAR) AS NUM_PRESTAMOS " +
                "FROM PRESTAMO P " +
                "JOIN DETALLE_PRESTAMO DP ON P.ID = DP.ID_PRESTAMO " +
                "JOIN EJEMPLAR E ON DP.ID_EJEMPLAR = E.ID " +
                "JOIN LIBRO L ON E.ID_LIBRO = L.ID " +
                "WHERE P.FECHA_PRESTAMO >= ADD_MONTHS(SYSDATE, -1) " +
                "GROUP BY L.TITULO " +
                "ORDER BY NUM_PRESTAMOS DESC " +
                "FETCH FIRST 5 ROWS ONLY";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> bookData = new HashMap<>();
                bookData.put("titulo", rs.getString("TITULO"));
                bookData.put("num_prestamos", rs.getInt("NUM_PRESTAMOS"));
                topBooks.add(bookData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener los libros más prestados: " + e.getMessage());
        }
        return topBooks;
    }

    @Override
    public List<Map<String, Object>> getTop5MostActiveClients() {
        List<Map<String, Object>> topClients = new ArrayList<>();
        // Consulta para obtener los 5 usuarios de biblioteca con más préstamos en el último mes
        String query = "SELECT UB.NOMBRE AS NOMBRE_USUARIO, COUNT(P.ID) AS NUM_PRESTAMOS " +
                "FROM PRESTAMO P " +
                "JOIN USUARIO_BIBLIOTECA UB ON P.ID_USUARIO = UB.ID " +
                "WHERE P.FECHA_PRESTAMO >= ADD_MONTHS(SYSDATE, -1) " +
                "GROUP BY UB.NOMBRE " +
                "ORDER BY NUM_PRESTAMOS DESC " +
                "FETCH FIRST 5 ROWS ONLY";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> clientData = new HashMap<>();
                clientData.put("nombre_usuario", rs.getString("NOMBRE_USUARIO"));
                clientData.put("num_prestamos", rs.getInt("NUM_PRESTAMOS"));
                topClients.add(clientData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al obtener los clientes más activos: " + e.getMessage());
        }
        return topClients;
    }
}