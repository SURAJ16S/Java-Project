package com.company.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
            
            createTables();
            
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
    
    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create user_accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS user_accounts (" +
                        "user_id VARCHAR(20) PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(100) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'active', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            // Create employees table
            stmt.execute("CREATE TABLE IF NOT EXISTS employees (" +
                        "employee_id VARCHAR(20) PRIMARY KEY, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "department VARCHAR(50), " +
                        "designation VARCHAR(50), " +
                        "FOREIGN KEY (employee_id) REFERENCES user_accounts(user_id))");
            
            // Create work_records table
            stmt.execute("CREATE TABLE IF NOT EXISTS work_records (" +
                        "record_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "employee_id VARCHAR(20) NOT NULL, " +
                        "work_date DATE NOT NULL, " +
                        "work_type VARCHAR(50) NOT NULL, " +
                        "task VARCHAR(100) NOT NULL, " +
                        "start_time TIME NOT NULL, " +
                        "end_time TIME NOT NULL, " +
                        "working_hours DECIMAL(5,2) NOT NULL, " +
                        "overtime_hours DECIMAL(5,2) NOT NULL, " +
                        "description TEXT, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (employee_id) REFERENCES employees(employee_id))");
            
            // Create salary_calculations table
            stmt.execute("DROP TABLE IF EXISTS salary_calculations");
            stmt.execute("CREATE TABLE salary_calculations (" +
                        "calculation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "employee_id VARCHAR(20) NOT NULL, " +
                        "month INT NOT NULL, " +
                        "year INT NOT NULL, " +
                        "base_salary DECIMAL(10,2) NOT NULL, " +
                        "absent_deduction DECIMAL(10,2) DEFAULT 0, " +
                        "night_shift_allowance DECIMAL(10,2) DEFAULT 0, " +
                        "overtime_pay DECIMAL(10,2) DEFAULT 0, " +
                        "hourly_pay DECIMAL(10,2) DEFAULT 0, " +
                        "total_salary DECIMAL(10,2) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (employee_id) REFERENCES employees(employee_id))");
            
            // Create salary_payments table
            stmt.execute("CREATE TABLE IF NOT EXISTS salary_payments (" +
                        "payment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "calculation_id INT NOT NULL, " +
                        "payment_date DATE NOT NULL, " +
                        "amount DECIMAL(10,2) NOT NULL, " +
                        "payment_method VARCHAR(50) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'pending', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (calculation_id) REFERENCES salary_calculations(calculation_id))");
            
            // Create attendance table
            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                        "attendance_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "employee_id VARCHAR(20) NOT NULL, " +
                        "work_date DATE NOT NULL, " +
                        "shift_type VARCHAR(20) NOT NULL, " +
                        "start_time TIME NOT NULL, " +
                        "end_time TIME NOT NULL, " +
                        "working_hours DECIMAL(5,2) NOT NULL, " +
                        "status VARCHAR(20) DEFAULT 'present', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (employee_id) REFERENCES employees(employee_id))");
            
            // Create work_assignments table
            stmt.execute("CREATE TABLE IF NOT EXISTS work_assignments (" +
                        "assignment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "employee_id VARCHAR(20) NOT NULL, " +
                        "project_name VARCHAR(100) NOT NULL, " +
                        "task_description TEXT NOT NULL, " +
                        "start_date DATE NOT NULL, " +
                        "end_date DATE, " +
                        "status VARCHAR(20) DEFAULT 'pending', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (employee_id) REFERENCES employees(employee_id))");
            
            // Create activity_logs table
            stmt.execute("CREATE TABLE IF NOT EXISTS activity_logs (" +
                        "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id VARCHAR(20) NOT NULL, " +
                        "activity_type VARCHAR(50) NOT NULL, " +
                        "details TEXT, " +
                        "status VARCHAR(20) DEFAULT 'success', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES user_accounts(user_id))");
            
            LOGGER.info("Database tables created successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating database tables", e);
        }
    }
}

