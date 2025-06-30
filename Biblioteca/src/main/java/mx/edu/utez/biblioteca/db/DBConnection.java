package mx.edu.utez.biblioteca.db;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;


public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String URL = "jdbc:oracle:thin:@yc7sxaz34rkdswbh_high?TNS_ADMIN=F:/Wallets/Wallet_YC7SXAZ34RKDSWBH";
        Properties props = new Properties();
        props.setProperty("user", "ADMIN");
        props.setProperty("password", "zurisRA$2006");
        return DriverManager.getConnection(URL, props);
    }
}
