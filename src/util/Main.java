package util;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
    	

        try {
            Connection connection = DBConnUtil.getDBConn();
            if (connection != null) {
                System.out.println("Connection successful!");
                connection.close(); 
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver Class Not Found: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
