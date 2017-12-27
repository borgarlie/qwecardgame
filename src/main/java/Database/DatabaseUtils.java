package Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite://Users/borgarlie/sqlite/test.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
