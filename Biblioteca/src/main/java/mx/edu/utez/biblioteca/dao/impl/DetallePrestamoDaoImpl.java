package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IDetallePrestamo;
import mx.edu.utez.biblioteca.model.Ejemplar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetallePrestamoDaoImpl implements IDetallePrestamo {
    /**
     * Inserta un ejemplar dentro de un préstamo existente.
     * El campo DEVUELTO se inicializa como 'N' por defecto.
     */
    @Override
    public boolean insertarEjemplar(int id_prestamo, int id_ejemplar) {
        String sql = "INSERT INTO DETALLE_PRESTAMO (ID_PRESTAMO, ID_EJEMPLAR, DEVUELTO) VALUES (?, ?, 'N')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id_prestamo);
            pst.setInt(2, id_ejemplar);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    /**
     * Actualiza el estado de devolución de un ejemplar específico.
     */
    public boolean marcarDevuelto(int idDetallePrestamo, boolean devuelto) {
        String sql = "UPDATE DETALLE_PRESTAMO SET DEVUELTO = ? WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, devuelto ? "S" : "N");
            pst.setInt(2, idDetallePrestamo);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
   public boolean eliminarDetallesPorPrestamo(int idPrestamo) {
       String sql = "DELETE FROM DETALLE_PRESTAMO WHERE ID_PRESTAMO = ?";

       try (Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)) {

           pst.setInt(1, idPrestamo);
           int filas = pst.executeUpdate();
           return filas >= 0;

       } catch (SQLException e) {
           System.err.println("Error al eliminar detalles: " + e.getMessage());
           e.printStackTrace();
           return false;
       }
   }

   // Obtiene los ejemplares mediante el id del libro
   @Override
   public List<Ejemplar> findEjemplaresByPrestamoId(int prestamoId) {
       List<Ejemplar> ejemplares = new ArrayList<>();
       String query = "SELECT e.* FROM ejemplares e JOIN detalle_prestamos dp ON e.id = dp.id_ejemplar WHERE dp.id_prestamo = ?";

       try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {

           ps.setInt(1, prestamoId);
           ResultSet rs = ps.executeQuery();

           while (rs.next()) {
               Ejemplar ejemplar = new Ejemplar();
               ejemplar.setIdEjemplar(rs.getInt("id"));
               ejemplar.setCodigo(rs.getString("codigo"));
               ejemplar.setUbicacion(rs.getString("ubicacion"));
               ejemplares.add(ejemplar);
           }

       } catch (Exception e) {
           e.printStackTrace();
       }

       return ejemplares;
   }


}
