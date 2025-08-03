package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolDaoImpl {

    /**
     * Busca un rol en la base de datos por su nombre.
     * @param nombreRol El nombre del rol a buscar
     * @return El objeto Rol si se encuentra, o null si no existe.
     * @throws Exception Si ocurre un error de SQL.
     */
    public Rol findByNombre(String nombreRol) throws Exception {
        Rol rol = null;
        // La consulta SQL debe ser sobre la tabla ROL
        String sql = "SELECT ID, NOMBRE FROM ROL WHERE UPPER(NOMBRE) = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se busca el rol por el nombre en mayúsculas para evitar problemas de case-sensitivity
            ps.setString(1, nombreRol.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rol = new Rol();
                    rol.setId(rs.getInt("ID"));
                    rol.setNombre(rs.getString("NOMBRE"));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar rol por nombre: " + e.getMessage(), e);
        }

        return rol;
    }
}
