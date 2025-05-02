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
        String password = new String(passwordField.getPassword()).trim();
        String userType = (String) userTypeBox.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }
        
        try {
            Connection con = DBConnection.getConnection();
            String hashedPassword = hashPassword(password);
            
            String query = "SELECT * FROM user_accounts WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, hashedPassword);
            pst.setString(3, userType);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("user_id");
                statusLabel.setText("Login successful!");
                
                if (userType.equals("admin")) {
                    new AdminDashboard().setVisible(true);
                } else if (userType.equals("developer")) {
                    new DeveloperDashboard(userId).setVisible(true);
                } else {
                    new EmployeeDashboard(userId).setVisible(true);
                }
                dispose();
            } else {
                statusLabel.setText("Invalid username, password, or user type.");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Login failed: " + ex.getMessage());
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
