package mx.edu.utez.biblioteca.dao;

import mx.edu.utez.biblioteca.db.DBConnection;

public class UserDAO {
    public boolean validateUser (String user, String password){
        String query = "SELECT COUNT(*) FROM users where username = ? and password = ?";
    }
}
