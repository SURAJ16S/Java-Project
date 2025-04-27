package com.company.main;

import com.company.database.DBConnection;
import com.company.gui.LoginFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize database connection
            System.out.println("Initializing database connection...");
            DBConnection.testConnection();
            
            // Set up the look and feel
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                System.out.println("FlatLaf initialized successfully.");
            } catch (Exception e) {
                System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    System.out.println("Using system look and feel instead.");
                } catch (Exception ex) {
                    System.err.println("Failed to set look and feel: " + ex.getMessage());
                }
            }
            
            // Start the application
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginFrame().setVisible(true);
                    System.out.println("Application started successfully.");
                } catch (Exception e) {
                    System.err.println("Failed to start application: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Failed to start application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Critical error during startup: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Critical error during startup: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
