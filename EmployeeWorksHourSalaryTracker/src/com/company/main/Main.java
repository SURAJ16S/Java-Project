package com.company.main;

import com.company.database.DBConnection;
import com.company.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Optionally test the MySQL connection.
        DBConnection.testConnection();
        
        // Launch the login screen.
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
