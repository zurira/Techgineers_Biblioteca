package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.dao.IUsuarioBiblioteca;
import mx.edu.utez.biblioteca.model.UsuarioBiblioteca;
import mx.edu.utez.biblioteca.config.DBConnection;

import java.io.ByteArrayOutputStream; // <-- Importación necesaria
import java.io.InputStream;            // <-- Importación necesaria
import java.io.File;                   // <-- Necesaria si el DAO actualiza la foto
import java.io.FileInputStream;        // <-- Necesaria si el DAO actualiza la foto
import java.io.IOException;            // <-- Necesaria
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioBibliotecaDaoImpl implements IUsuarioBiblioteca {

    private UsuarioBiblioteca buildUsuarioFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID");
        String nombre = rs.getString("NOMBRE");
        Date fechaNacimientoSql = rs.getDate("FECHA_NACIMIENTO");
        LocalDate fechaNacimiento = (fechaNacimientoSql != null) ? fechaNacimientoSql.toLocalDate() : null;
        String correo = rs.getString("CORREO");
        String telefono = rs.getString("TELEFONO");
        String direccion = rs.getString("DIRECCION");
        String estado = rs.getString("ESTADO");

        return new UsuarioBiblioteca(id, nombre, fechaNacimiento, correo, telefono, direccion, estado);
    }

    @Override
    public List<UsuarioBiblioteca> findAll() throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(buildUsuarioFromResultSet(rs));
            }
        }
        return usuarios;
    }

    @Override
    public UsuarioBiblioteca findById(int id) throws Exception {
        UsuarioBiblioteca usuario = null;
        // Solo para los datos básicos, la foto se obtiene con getFotografiaById()
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = buildUsuarioFromResultSet(rs);
                }
            }
        }
        return usuario;
    }

    @Override
    public void create(UsuarioBiblioteca usuario) throws Exception {
        // Tu método create original, NO incluye fotografía.
        // Si la fotografía siempre se inserta al crear, aquí tendrías que pasarla o llamar a otro método.
        // Por simplicidad para este ejemplo, asumo que la foto se actualizará después o en otro método.
        String query = "INSERT INTO USUARIO_BIBLIOTECA (NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    // Método para crear un usuario incluyendo la fotografía (SOBRECARGA)
    public void create(UsuarioBiblioteca usuario, File fotoFile) throws Exception {
        String query = "INSERT INTO USUARIO_BIBLIOTECA (NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO, FOTOGRAFIA) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());

            if (fotoFile != null) {
                try (FileInputStream fis = new FileInputStream(fotoFile)) {
                    ps.setBinaryStream(7, fis, (int) fotoFile.length());
                }
            } else {
                ps.setNull(7, Types.BLOB);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }


    @Override
    public void update(UsuarioBiblioteca usuario) throws Exception {
        // Tu método update original, NO incluye fotografía.
        // Esto significa que si se llama a este método, el campo FOTOGRAFIA no se actualizará.
        // Si necesitas actualizar la foto, usarías el método update(UsuarioBiblioteca, File).
        String query = "UPDATE USUARIO_BIBLIOTECA SET NOMBRE = ?, FECHA_NACIMIENTO = ?, CORREO = ?, TELEFONO = ?, DIRECCION = ?, ESTADO = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());
            ps.setInt(7, usuario.getId());
            ps.executeUpdate();
        }
    }

    // Método para actualizar un usuario incluyendo la fotografía (SOBRECARGA)
    public void update(UsuarioBiblioteca usuario, File fotoFile) throws Exception {
        // Este método actualizará la fotografía. Si fotoFile es null, la pondrá a NULL en la DB.
        String query = "UPDATE USUARIO_BIBLIOTECA SET NOMBRE = ?, FECHA_NACIMIENTO = ?, CORREO = ?, TELEFONO = ?, DIRECCION = ?, ESTADO = ?, FOTOGRAFIA = ? WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getDireccion());
            ps.setString(6, usuario.getEstado());

            if (fotoFile != null) {
                try (FileInputStream fis = new FileInputStream(fotoFile)) {
                    ps.setBinaryStream(7, fis, (int) fotoFile.length());
                }
            } else {
                ps.setNull(7, Types.BLOB); // Si no se selecciona nueva foto, se pone NULL
            }

            ps.setInt(8, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String query = "DELETE FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<UsuarioBiblioteca> search(String searchTerm) throws Exception {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO FROM USUARIO_BIBLIOTECA " +
                "WHERE LOWER(NOMBRE) LIKE ? OR LOWER(CORREO) LIKE ? OR LOWER(TELEFONO) LIKE ? OR LOWER(DIRECCION) LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(buildUsuarioFromResultSet(rs));
                }
            }
        }
        return usuarios;
    }

    // NUEVO MÉTODO PARA OBTENER LA FOTOGRAFÍA (Necesario para VerUsuarioController)
    public byte[] getFotografiaById(int idUsuario) throws Exception {
        String query = "SELECT FOTOGRAFIA FROM USUARIO_BIBLIOTECA WHERE ID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InputStream is = rs.getBinaryStream("FOTOGRAFIA");
                    if (is != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, bytesRead);
                        }
                        return baos.toByteArray();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer la fotografía desde la base de datos: " + e.getMessage());
            throw new Exception("Error al leer la fotografía", e);
        }
        return null; // Retorna null si no hay foto o si el usuario no existe
    }
}