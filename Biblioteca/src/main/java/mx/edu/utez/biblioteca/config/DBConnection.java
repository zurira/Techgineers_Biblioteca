package mx.edu.utez.biblioteca.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String WALLET_PATH = "F:/Downloads/Wallet_YC7SXAZ34RKDSWBH";

    private static final String URL = "jdbc:oracle:thin:@yc7sxaz34rkdswbh_high?TNS_ADMIN=F:/Downloads/Wallet_YC7SXAZ34RKDSWBH";
    private static final String USER = "ADMIN";
    private static final String PASSWORD = "zurisRA$2006";

    public static Connection getConnection() {
        try {
            // Configura propiedades del wallet
            System.setProperty("oracle.net.tns_admin", WALLET_PATH);
            System.setProperty("oracle.net.ssl_server_dn_match", "true");

            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
            return null;
        }
    }

    // Para probar la conexión
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("¡Conexión exitosa!");
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
}

