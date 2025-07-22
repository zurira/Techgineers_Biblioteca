package mx.edu.utez.biblioteca.config;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            String alias = "bdutez_high"; // Usa el alias definido en tu tnsnames.ora
            String tnsAdminPath = "C:\\Users\\letic\\Downloads\\Wallet_L9I11J33YFKVI0T1"; // ruta donde se descomprime la wallet

            String url = "jdbc:oracle:thin:@"+alias+"?TNS_ADMIN="+tnsAdminPath;
            String user = "L9I11J33YFKVI0T1"; //  usuario
            String pass = "Techgineers123"; //  contraseña

            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println(" Error al conectar a Oracle con Wallet:");
            e.printStackTrace();
            return null;
        }
    }
}