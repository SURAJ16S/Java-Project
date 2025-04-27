package com.company.main;

import com.company.database.DBConnection;
import com.company.gui.HomeFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection
        System.out.println("Database connection initialized successfully.");
        DBConnection.testConnection();
        
        // Set up the look and feel
        try {
            // Try to use FlatLaf
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            System.out.println("FlatLaf initialized successfully.");
        } catch (Exception e) {
            // If FlatLaf fails, use the system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Using system look and feel instead.");
            } catch (Exception ex) {
                System.err.println("Failed to set look and feel: " + ex.getMessage());
            }
        }
        
        // Start the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }
}
