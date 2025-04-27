package com.company.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/EmployeeDB";
    private static final String USER = "root";            // Change as required
    private static final String PASSWORD = "bugsploit"; // Change as required

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Failed to connect to MySQL database.");
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    // Method to test connection (run this to see console output)
    public static void testConnection() {
        Connection con = getConnection();
        if (con != null) {
            System.out.println("MySQL Connection successful!");
        } else {
            System.out.println("MySQL Connection failed.");
        }
    }
}
