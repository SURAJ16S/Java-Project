package com.company.gui;

import com.company.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;
    private JLabel statusLabel;
    
    public LoginFrame() {
        setTitle("Employee Tracker Login");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents() {
        // Main panel with a nice background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with title and job application button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 245));
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Employee Tracker Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
        topPanel.add(headerPanel, BorderLayout.CENTER);
        
        // Job Application button in top right
        JButton jobAppBtn = new JButton("Job Application");
        jobAppBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jobAppBtn.setBackground(new Color(70, 130, 180));
        jobAppBtn.setForeground(Color.WHITE);
        jobAppBtn.setFocusPainted(false);
        jobAppBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        jobAppBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                jobAppBtn.setBackground(new Color(60, 110, 160));
            }
            public void mouseExited(MouseEvent e) {
                jobAppBtn.setBackground(new Color(70, 130, 180));
            }
        });
        jobAppBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openJobApplication();
            }
        });
        topPanel.add(jobAppBtn, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Form panel with a white background
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        
        // User Type
        JLabel userTypeLabel = new JLabel("User Type:");
        userTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(userTypeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        userTypeBox = new JComboBox<>(new String[]{"admin", "employee", "developer"});
        userTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(userTypeBox, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(passwordField, gbc);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 70, 70));
        formPanel.add(statusLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton loginBtn = createStyledButton("Login");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        JButton registerBtn = createStyledButton("Register");
        registerBtn.setBackground(new Color(70, 130, 180));
        registerBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerBtn.setBackground(new Color(60, 110, 160));
            }
            public void mouseExited(MouseEvent e) {
                registerBtn.setBackground(new Color(70, 130, 180));
            }
        });
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });
        
        JButton exitBtn = createStyledButton("Exit");
        exitBtn.setBackground(new Color(180, 70, 70));
        exitBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                exitBtn.setBackground(new Color(160, 60, 60));
            }
            public void mouseExited(MouseEvent e) {
                exitBtn.setBackground(new Color(180, 70, 70));
            }
        });
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        buttonPanel.add(exitBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add enter key listener for login
        getRootPane().setDefaultButton(loginBtn);
    }
    
    private void openJobApplication() {
        new JobApplicationForm().setVisible(true);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 110, 160));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        return button;
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Connection con = DBConnection.getConnection();
            System.out.println("Attempting login for username: " + username);
            
            // First check if it's an employee trying to login with default credentials
            String checkEmployeeQuery = "SELECT employee_id FROM employees WHERE employee_id = ?";
            PreparedStatement checkEmployeeStmt = con.prepareStatement(checkEmployeeQuery);
            checkEmployeeStmt.setString(1, username);
            ResultSet employeeRs = checkEmployeeStmt.executeQuery();
            
            if (employeeRs.next() && username.equals(password)) {
                System.out.println("Employee found with default credentials");
                // Check if they already have a user account
                String checkUserQuery = "SELECT user_id FROM user_accounts WHERE user_id = ?";
                PreparedStatement checkUserStmt = con.prepareStatement(checkUserQuery);
                checkUserStmt.setString(1, username);
                ResultSet userRs = checkUserStmt.executeQuery();
                
                if (!userRs.next()) {
                    // Create user account with default credentials
                    String hashedPassword = hashPassword(username); // Hash the default password
                    String insertUserQuery = "INSERT INTO user_accounts (user_id, username, password, role) VALUES (?, ?, ?, 'employee')";
                    PreparedStatement insertUserStmt = con.prepareStatement(insertUserQuery);
                    insertUserStmt.setString(1, username);
                    insertUserStmt.setString(2, username);
                    insertUserStmt.setString(3, hashedPassword);
                    insertUserStmt.executeUpdate();
                }
                
                // Log the activity
                String logQuery = "INSERT INTO activity_logs (user_id, activity_type, details) VALUES (?, 'login', 'Employee logged in with default credentials')";
                PreparedStatement logStmt = con.prepareStatement(logQuery);
                logStmt.setString(1, username);
                logStmt.executeUpdate();
                
                // Open employee dashboard
                new EmployeeDashboard(username).setVisible(true);
                dispose();
                return;
            }
            
            // Check regular user credentials (for admin and other users)
            String query = "SELECT user_id, username, password, role FROM user_accounts WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String hashedInputPassword = hashPassword(password);
                String role = rs.getString("role");
                
                if (hashedInputPassword.equals(storedPassword)) {
                    // Log the activity
                    String logQuery = "INSERT INTO activity_logs (user_id, activity_type, details) VALUES (?, 'login', 'User logged in successfully')";
                    PreparedStatement logStmt = con.prepareStatement(logQuery);
                    logStmt.setString(1, rs.getString("user_id"));
                    logStmt.executeUpdate();
                    
                    // Open appropriate dashboard based on role
                    if (role.equals("admin")) {
                        new AdminDashboard().setVisible(true);
                    } else if (role.equals("employee")) {
                        new EmployeeDashboard(rs.getString("user_id")).setVisible(true);
                    } else if (role.equals("developer")) {
                        new DeveloperDashboard(rs.getString("user_id")).setVisible(true);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid password", 
                        "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("No user found with username: " + username);
                // Check if admin account exists, if not create it
                if (username.equals("admin") && password.equals("admin123")) {
                    String checkAdminQuery = "SELECT user_id FROM user_accounts WHERE username = 'admin'";
                    PreparedStatement checkAdminStmt = con.prepareStatement(checkAdminQuery);
                    ResultSet adminRs = checkAdminStmt.executeQuery();
                    
                    if (!adminRs.next()) {
                        // Create admin account with hashed password
                        String hashedPassword = hashPassword("admin123");
                        String insertAdminQuery = "INSERT INTO user_accounts (user_id, username, password, role) VALUES ('ADMIN001', 'admin', ?, 'admin')";
                        PreparedStatement insertAdminStmt = con.prepareStatement(insertAdminQuery);
                        insertAdminStmt.setString(1, hashedPassword);
                        insertAdminStmt.executeUpdate();
                        
                        // Log the activity
                        String logQuery = "INSERT INTO activity_logs (user_id, activity_type, details) VALUES ('ADMIN001', 'login', 'Admin account created and logged in')";
                        PreparedStatement logStmt = con.prepareStatement(logQuery);
                        logStmt.executeUpdate();
                        
                        new AdminDashboard().setVisible(true);
                        dispose();
                        return;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "User not found", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            System.out.println("Error during login: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error during login: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain password if hashing fails
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
