package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IPrestamo;
import mx.edu.utez.biblioteca.model.Prestamo;
import java.sql.Connection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class PrestamoDaoImpl implements IPrestamo {
    @Override
    public int insertar(Prestamo prestamo) {
        String sql = "INSERT INTO PRESTAMO (ID_USUARIO, CORREO, FECHA_PRESTAMO, FECHA_LIMITE, FECHA_DEVOLUCION, ESTADO) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, new String[] {"ID"})) {

            pst.setInt(1, prestamo.getIdUsuario());
            pst.setString(2, prestamo.getCorreo());
            pst.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            pst.setDate(4, Date.valueOf(prestamo.getFechaLimite()));

            if (prestamo.getFechaDevolucion() != null) {
                pst.setDate(5, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                pst.setNull(5, Types.DATE);
            }

            pst.setString(6, prestamo.getEstado());

            int filas = pst.executeUpdate();

            if (filas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ← más seguro que usar "ID"
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}

