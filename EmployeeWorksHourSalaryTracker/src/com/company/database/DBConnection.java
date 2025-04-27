package com.company.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static final int MAX_POOL_SIZE = 10;
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final int IDLE_TIMEOUT = 600000; // 10 minutes
    
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    
    private static final ConcurrentHashMap<String, Connection> connectionPool = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    static {
        try {
            // Initialize logger
            FileHandler fileHandler = new FileHandler("database.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            
            // Load configuration
            loadConfiguration();
            
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Schedule connection cleanup
            scheduler.scheduleAtFixedRate(DBConnection::cleanupIdleConnections, 
                                         IDLE_TIMEOUT, IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
            
            LOGGER.info("Database connection system initialized successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load configuration file.", e);
        }
    }

    private static void loadConfiguration() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            // If config file doesn't exist, use default values
            LOGGER.warning("Configuration file not found. Using default values.");
            URL = "jdbc:mysql://localhost:3306/EmployeeDB";
            USER = "root";
            PASSWORD = "bugsploit";
            return;
        }
        
        URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/EmployeeDB");
        USER = props.getProperty("db.user", "root");
        PASSWORD = props.getProperty("db.password", "bugsploit");
        
        LOGGER.info("Database configuration loaded successfully.");
    }

    public static Connection getConnection() throws SQLException {
        String threadId = Thread.currentThread().getName();
        Connection conn = connectionPool.get(threadId);
        
        if (conn == null || conn.isClosed() || !conn.isValid(1)) {
            conn = createConnection();
            connectionPool.put(threadId, conn);
            LOGGER.info("New connection created for thread: " + threadId);
        }
        
        return conn;
    }
    
    private static Connection createConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create database connection.", e);
            throw e;
        }
    }
    
    private static void cleanupIdleConnections() {
        connectionPool.forEach((threadId, conn) -> {
            try {
                if (conn.isClosed() || !conn.isValid(1)) {
                    connectionPool.remove(threadId);
                    LOGGER.info("Removed invalid connection for thread: " + threadId);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error checking connection validity.", e);
            }
        });
    }
    
    public static void closeConnection() {
        String threadId = Thread.currentThread().getName();
        Connection conn = connectionPool.remove(threadId);
        if (conn != null) {
            try {
                conn.close();
                LOGGER.info("Connection closed for thread: " + threadId);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection.", e);
            }
        }
    }
    
    // Method to test connection (run this to see console output)
    public static void testConnection() {
        try {
            Connection con = getConnection();
            if (con != null && con.isValid(1)) {
                LOGGER.info("MySQL Connection successful!");
                System.out.println("MySQL Connection successful!");
            } else {
                LOGGER.warning("MySQL Connection failed.");
                System.out.println("MySQL Connection failed.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error testing connection.", e);
            System.out.println("MySQL Connection failed: " + e.getMessage());
        }
    }
    
    // Shutdown the connection pool
    public static void shutdown() {
        connectionPool.forEach((threadId, conn) -> {
            try {
                conn.close();
                LOGGER.info("Connection closed during shutdown for thread: " + threadId);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection during shutdown.", e);
            }
        });
        connectionPool.clear();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOGGER.info("Database connection pool shut down successfully.");
    }
}

