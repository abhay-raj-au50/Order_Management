package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {
    private static Connection connection;

    public static Connection getDBConn() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            // Load properties file
            Properties properties = new Properties();
            try (InputStream input = DBConnUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find db.properties");
                    return null;
                }
                properties.load(input);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException("Failed to load properties file.", e);
            }

            // Get properties
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            String driver = properties.getProperty("db.driver");

            // Load JDBC driver
            Class.forName(driver);
            // Establish the connection
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
