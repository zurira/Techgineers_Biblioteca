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
        byte[] fotografia = rs.getBytes("FOTOGRAFIA"); // <-- Se añade la foto aquí

        UsuarioBiblioteca usuario = new UsuarioBiblioteca(id, nombre, fechaNacimiento, correo, telefono, direccion, estado);
        usuario.setFotografia(fotografia); // <-- Se añade la foto al objeto
        return usuario;

    }

    @Override
    public List<UsuarioBiblioteca> findAll() {
        List<UsuarioBiblioteca> usuarios = new ArrayList<>();
        String query = "SELECT * FROM USUARIO_BIBLIOTECA";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UsuarioBiblioteca usuario = new UsuarioBiblioteca();
                usuario.setId(rs.getInt("ID"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
                usuario.setCorreo(rs.getString("CORREO"));
                usuario.setTelefono(rs.getString("TELEFONO"));
                usuario.setEstado(rs.getString("ESTADO"));
                usuario.setDireccion(rs.getString("DIRECCION"));

                usuarios.add(usuario);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
    @Override
    public UsuarioBiblioteca findById(int id) throws Exception {
        UsuarioBiblioteca usuario = null;
        // Solo para los datos básicos, la foto se obtiene con getFotografiaById()
        String query = "SELECT ID, NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO, FOTOGRAFIA FROM USUARIO_BIBLIOTECA WHERE ID = ?";
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

    public void create(UsuarioBiblioteca usuario, File fotoFile) throws Exception {
        String query = "INSERT INTO USUARIO_BIBLIOTECA (NOMBRE, FECHA_NACIMIENTO, CORREO, TELEFONO, DIRECCION, ESTADO, FOTOGRAFIA) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query, new String[]{"ID"}))  {
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
                    System.out.println("ID del préstamo generado: " + usuario.getId());
                }
            }
        }
    }


    // Reemplaza el método update en tu UsuarioBibliotecaDaoImpl con este código
    @Override
    public void update(UsuarioBiblioteca usuario) throws Exception {
        String query = "UPDATE USUARIO_BIBLIOTECA SET NOMBRE = ?, FECHA_NACIMIENTO = ?, CORREO = ?, TELEFONO = ?, DIRECCION = ?, ESTADO = ?, FOTOGRAFIA = ? WHERE ID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            // 1. Asigna el nombre
            ps.setString(1, usuario.getNombre());

            // 2. Asigna la fecha. Asegúrate de que no es nula.
            if (usuario.getFechaNacimiento() != null) {
                ps.setDate(2, Date.valueOf(usuario.getFechaNacimiento()));
            } else {
                ps.setNull(2, Types.DATE); // Si es nula, asigna un valor NULL
            }

            // 3. Asigna el correo
            ps.setString(3, usuario.getCorreo());

            // 4. Asigna el teléfono
            ps.setString(4, usuario.getTelefono());

            // 5. Asigna la dirección
            ps.setString(5, usuario.getDireccion());

            // 6. Asigna el estado. Esto podría ser el causante del error.
            if (usuario.getEstado() != null) {
                ps.setString(6, usuario.getEstado());
            } else {
                ps.setNull(6, Types.VARCHAR); // Asigna NULL si el estado es nulo
            }

            // 7. Asigna la foto
            if (usuario.getFotografia() != null) {
                ps.setBytes(7, usuario.getFotografia());
            } else {
                ps.setNull(7, Types.BLOB);
            }

            // 8. Asigna el ID para el WHERE
            ps.setInt(8, usuario.getId());

            ps.executeUpdate();
        }
    }

    public void update(UsuarioBiblioteca usuario, File fotoFile) throws Exception {
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
