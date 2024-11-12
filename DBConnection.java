import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/researchpractice";
    private static final String USER = "root";  // Replace with your DB username
    private static final String PASSWORD = "root";  // Replace with your DB password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
        //jdbc:mysql://127.0.0.1:3306/?user=root
        //jdbc:mysql://127.0.0.1:3306/?user=root
        //jdbc:mysql://<127.0.0.1>:<3306/<researchpractice>?user=<username>&password=<password>

    }
}
