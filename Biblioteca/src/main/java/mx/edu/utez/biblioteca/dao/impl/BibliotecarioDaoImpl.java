package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IBibliotecario;
import mx.edu.utez.biblioteca.model.Bibliotecario;
import mx.edu.utez.biblioteca.model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BibliotecarioDaoImpl implements IBibliotecario {

    private Bibliotecario extractBibliotecarioFromResultSet(ResultSet rs) throws SQLException {
        Bibliotecario bibliotecario = new Bibliotecario();
        bibliotecario.setId(rs.getInt("ID"));
        bibliotecario.setNombre(rs.getString("NOMBRE"));
        bibliotecario.setCorreo(rs.getString("CORREO"));
        bibliotecario.setTelefono(rs.getString("TELEFONO"));
        bibliotecario.setUsername(rs.getString("USERNAME"));
        bibliotecario.setPassword(rs.getString("PASSWORD"));
        bibliotecario.setFoto(rs.getBytes("FOTO")); // Asumiendo que FOTO es BLOB
        bibliotecario.setDireccion(rs.getString("DIRECCION"));
        bibliotecario.setEstado(rs.getString("ESTADO"));

        // Cargar el rol
        int idRol = rs.getInt("ID_ROL");
        String nombreRol = rs.getString("NOMBRE_ROL");
        if (idRol != 0 && nombreRol != null) {
            Rol rol = new Rol();
            rol.setId(idRol);
            rol.setNombre(nombreRol);
            bibliotecario.setRol(rol);
        }

        return bibliotecario;
    }

    private final String BASE_QUERY = "SELECT u.ID, u.NOMBRE, u.CORREO, u.TELEFONO, u.USERNAME, u.PASSWORD, u.FOTO, u.DIRECCION, u.ESTADO, " +
            "r.ID AS ID_ROL, r.NOMBRE AS NOMBRE_ROL " +
            "FROM USUARIOS u " +
            "LEFT JOIN ROLES r ON u.ID_ROL = r.ID ";

    @Override
    public List<Bibliotecario> findAll() throws Exception {
        List<Bibliotecario> bibliotecarios = new ArrayList<>();
        String query = BASE_QUERY + "WHERE r.NOMBRE = 'Bibliotecario' AND u.ESTADO = 'S'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                bibliotecarios.add(extractBibliotecarioFromResultSet(rs));
            }
        }
        return bibliotecarios;
    }

    @Override
    public Bibliotecario findById(int id) throws Exception {
        Bibliotecario bibliotecario = null;
        String query = BASE_QUERY + "WHERE u.ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bibliotecario = extractBibliotecarioFromResultSet(rs);
                }
            }
        }
        return bibliotecario;
    }

    @Override
    public boolean create(Bibliotecario bibliotecario) throws Exception {
        String query = "INSERT INTO USUARIOS (NOMBRE, CORREO, TELEFONO, USERNAME, PASSWORD, FOTO, ID_ROL, ESTADO, DIRECCION) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, bibliotecario.getNombre());
            pstmt.setString(2, bibliotecario.getCorreo());
            pstmt.setString(3, bibliotecario.getTelefono());
            pstmt.setString(4, bibliotecario.getUsername());
            pstmt.setString(5, bibliotecario.getPassword());
            pstmt.setBytes(6, bibliotecario.getFoto());
            pstmt.setInt(7, bibliotecario.getRol().getId());
            pstmt.setString(8, bibliotecario.getEstado());
            pstmt.setString(9, bibliotecario.getDireccion());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean update(Bibliotecario bibliotecario) throws Exception {
        String query = "UPDATE USUARIOS SET NOMBRE = ?, CORREO = ?, TELEFONO = ?, USERNAME = ?, PASSWORD = ?, FOTO = ?, ID_ROL = ?, ESTADO = ?, DIRECCION = ? " +
                "WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, bibliotecario.getNombre());
            pstmt.setString(2, bibliotecario.getCorreo());
            pstmt.setString(3, bibliotecario.getTelefono());
            pstmt.setString(4, bibliotecario.getUsername());
            pstmt.setString(5, bibliotecario.getPassword());
            pstmt.setBytes(6, bibliotecario.getFoto());
            pstmt.setInt(7, bibliotecario.getRol().getId());
            pstmt.setString(8, bibliotecario.getEstado());
            pstmt.setString(9, bibliotecario.getDireccion());
            pstmt.setInt(10, bibliotecario.getId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String query = "DELETE FROM USUARIOS WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public List<Bibliotecario> search(String searchTerm) throws Exception {
        List<Bibliotecario> bibliotecarios = new ArrayList<>();
        String query = BASE_QUERY + "WHERE (LOWER(u.NOMBRE) LIKE ? OR LOWER(u.CORREO) LIKE ? OR LOWER(u.USERNAME) LIKE ?) AND r.NOMBRE = 'Bibliotecario'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bibliotecarios.add(extractBibliotecarioFromResultSet(rs));
                }
            }
        }
        return bibliotecarios;
    }

    @Override
    public boolean updateStatus(int idBibliotecario, String estado) throws Exception {
        String query = "UPDATE USUARIOS SET ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, idBibliotecario);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public Bibliotecario findByUsername(String username) throws Exception {
        Bibliotecario bibliotecario = null;
        String query = BASE_QUERY + "WHERE u.USERNAME = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bibliotecario = extractBibliotecarioFromResultSet(rs);
                }
            }
        }
        return bibliotecario;
    }
}