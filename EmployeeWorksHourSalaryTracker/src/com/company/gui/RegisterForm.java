package com.company.gui;

import com.company.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JComboBox<String> roleBox;
    
    public RegisterForm() {
        setTitle("User Registration");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        panel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
        panel.add(fullNameField);
        
        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"admin", "employee"});
        panel.add(roleBox);
        
        JButton regBtn = new JButton("Register");
        regBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(regBtn);
        bottomPanel.add(backBtn);
        
        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String role = (String) roleBox.getSelectedItem();
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO user_accounts(username, password, role) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password); // For production, use hashed passwords.
            pst.setString(3, role);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            new LoginFrame().setVisible(true);
            dispose();
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
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
                new RegisterForm().setVisible(true);
            }
        });
    }
}
