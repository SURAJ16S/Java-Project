package com.company.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Employee Works Hour & Salary Tracker - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        
        JButton loginBtn = new JButton("Login");
        // For demonstration, a simple login prompt.
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userType = JOptionPane.showInputDialog("Enter user type (admin, employee, developer):");
                if (userType != null) {
                    if (userType.equalsIgnoreCase("admin")) {
                        new AdminDashboard().setVisible(true);
                    } else if (userType.equalsIgnoreCase("employee")) {
                        String empId = JOptionPane.showInputDialog("Enter Employee ID:");
                        new EmployeeDashboard(empId).setVisible(true);
                    } else if (userType.equalsIgnoreCase("developer")) {
                        new DeveloperDashboard().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid user type.");
                        return;
                    }
                    dispose();
                }
            }
        });
        
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterForm().setVisible(true);
                dispose();
            }
        });
        
        // Application Form button from job seekers (guest)
        JButton applicationFormBtn = new JButton("Application Form");
        applicationFormBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ApplicationForm().setVisible(true);
                dispose();
            }
        });
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        panel.add(loginBtn);
        panel.add(registerBtn);
        panel.add(applicationFormBtn);
        panel.add(exitBtn);
        
        add(panel, BorderLayout.CENTER);
    }
}
