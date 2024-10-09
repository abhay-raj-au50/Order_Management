package util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {
    public static String getConnectionString() {
        Properties properties = new Properties();
        String connectionString = null;

        // Load properties file using ClassLoader
        try (InputStream input = DBPropertyUtil.class.getClassLoader().getResourceAsStream("util/db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                return null;
            }
            properties.load(input);
            String dbUrl = properties.getProperty("db.url");
            String dbUsername = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");

            // Construct the connection string
            connectionString = String.format("jdbc:mysql://%s:%s@%s", dbUsername, dbPassword, dbUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connectionString;
    }
}
