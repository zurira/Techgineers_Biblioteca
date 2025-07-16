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
    public boolean login(String correo, String pass) throws Exception {
        String sql="SELECT ID,CORREO,PASSWORD FROM USUARIO WHERE CORREO=? and PASSWORD=?";
        try {
            Connection con = DBConnection.getConnection(); // se estable la conexion
            PreparedStatement ps =  con.prepareStatement(sql); //se prepara la consulta para evitar la inyecion de SQL
            ps.setString(1,correo);
            ps.setString(2, pass);
            ResultSet resultSet=ps.executeQuery(); //se ejecuta la consulta
            if(resultSet.next()){
                return true;
            }
            return false;
        }catch (Exception e){
            throw new Exception(e);
        }

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

