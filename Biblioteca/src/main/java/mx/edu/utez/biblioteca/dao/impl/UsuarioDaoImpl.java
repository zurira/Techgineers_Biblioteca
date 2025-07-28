package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> TTS17

public class UsuarioDaoImpl implements IUsuario {

    @Override
<<<<<<< HEAD
    public Usuario login(String correo, String pass) throws Exception {
        Usuario usuario = null;
        String sql = "SELECT u.ID, u.CORREO, u.PASSWORD, r.ID AS ID_ROL, r.NOMBRE AS NOMBRE_ROL " +
                "FROM USUARIO u " +
                "JOIN ROL r ON u.ID_ROL = r.ID " +
                "WHERE u.CORREO = ? AND u.PASSWORD = ?";
        try {
            Connection con = DBConnection.getConnection(); // se estable la conexion
            PreparedStatement ps =  con.prepareStatement(sql); //se prepara la consulta para evitar la inyecion de SQL
            ps.setString(1,correo);
            ps.setString(2, pass);
            ResultSet resultSet=ps.executeQuery(); //se ejecuta la consulta
            if(resultSet.next()){
                usuario = new Usuario();
                usuario.setId(resultSet.getInt("ID"));
                usuario.setCorreo(resultSet.getString("CORREO"));
                usuario.setPassword(resultSet.getString("PASSWORD"));
                usuario.setRol(resultSet.getString("ID_ROL"));
                usuario.setNombreRol(resultSet.getString("NOMBRE_ROL"));
            }
        }catch (Exception e){
            throw new Exception(e);
        }
        return usuario;

    }

    public static void main(String[] args) {
        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            System.out.println(dao.login("",""));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
=======
    public int obtenerIdPorNombre(String nombre) {
        String sql = "SELECT ID FROM USUARIO_BIBLIOTECA WHERE NOMBRE = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nombre);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    @Override
    public List<String> obtenerTodosLosNombres() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT NOMBRE FROM USUARIO_BIBLIOTECA";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("NOMBRE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombres;
    }

}

>>>>>>> TTS17
