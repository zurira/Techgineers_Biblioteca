package mx.edu.utez.biblioteca.dao.impl;

import mx.edu.utez.biblioteca.config.DBConnection;
import mx.edu.utez.biblioteca.dao.IUsuario;
import mx.edu.utez.biblioteca.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDaoImpl implements IUsuario {

    @Override
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
