package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IDetallePrestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DetallePrestamoDaoImpl implements IDetallePrestamo {
    /**
     * Inserta un ejemplar dentro de un préstamo existente.
     * El campo DEVUELTO se inicializa como 'N' por defecto.
     */
    @Override
    public void insertarEjemplar(int id_prestamo, int id_ejemplar) {
        String sql = "INSERT INTO DETALLE_PRESTAMO (ID_PRESTAMO, ID_EJEMPLAR, DEVUELTO) VALUES (?, ?, 'N')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id_prestamo);
            pst.setInt(2, id_ejemplar);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void marcarDevuelto(int idDetallePrestamo, boolean devuelto) {

    }
}
