import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    static Connection con; // global connection object

    public static Connection getConnection() {
        try {
            String mysqlJDBCDriver = "com.mysql.cj.jdbc.Driver"; // jdbc driver
            String url = "jdbc:mysql://localhost:3306/BANK";
            String user = "root";
            String pass = "enter your password";
            Class.forName(mysqlJDBCDriver);
            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.out.println("Connection Failed!");
        }

        return con;
    }
}
