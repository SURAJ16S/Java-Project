package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class EmployeeDashboard extends JFrame {
    private String employeeId;
    private JLabel statusBar;
    
    public EmployeeDashboard(String empId) {
        this.employeeId = empId;
        setTitle("Employee Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents(){
        // Main panel with a nice background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with title and employee info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 245));
        
        // Title
        JLabel titleLabel = new JLabel("Employee Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Employee info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(240, 240, 245));
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT full_name FROM employees WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JLabel welcomeLabel = new JLabel("Welcome, " + rs.getString("full_name"));
                welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                welcomeLabel.setForeground(new Color(70, 70, 70));
                infoPanel.add(welcomeLabel);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Clock In/Out button
        JButton clockBtn = createStyledButton("Clock In/Out");
        clockBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clockInOut();
            }
        });
        buttonPanel.add(clockBtn, gbc);
        
        // View Attendance button
        gbc.gridy = 1;
        JButton attendanceBtn = createStyledButton("View Attendance");
        attendanceBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAttendance();
            }
        });
        buttonPanel.add(attendanceBtn, gbc);
        
        // View Salary button
        gbc.gridy = 2;
        JButton salaryBtn = createStyledButton("View Salary");
        salaryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSalary();
            }
        });
        buttonPanel.add(salaryBtn, gbc);
        
        // Logout button
        gbc.gridy = 3;
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.setBackground(new Color(180, 70, 70));
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(160, 60, 60));
            }
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(new Color(180, 70, 70));
            }
        });
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(logoutBtn, gbc);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Status bar
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(new Color(100, 100, 100));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(230, 230, 230));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusPanel.add(statusBar, BorderLayout.WEST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void clockInOut() {
        try {
            Connection con = DBConnection.getConnection();
            
            // Check if there's already a record for today
            String checkQuery = "SELECT * FROM attendance WHERE employee_id = ? AND work_date = CURDATE()";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, employeeId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update end time and calculate working hours
                String updateQuery = "UPDATE attendance SET end_time = NOW(), " +
                                   "working_hours = TIMESTAMPDIFF(HOUR, start_time, NOW()) " +
                                   "WHERE employee_id = ? AND work_date = CURDATE()";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setString(1, employeeId);
                updateStmt.executeUpdate();
                statusBar.setText("Clock out recorded successfully!");
            } else {
                // Insert new clock in record
                String insertQuery = "INSERT INTO attendance (employee_id, work_date, shift_type, start_time) " +
                                   "VALUES (?, CURDATE(), 'Regular', NOW())";
                PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                insertStmt.setString(1, employeeId);
                insertStmt.executeUpdate();
                statusBar.setText("Clock in recorded successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error recording clock in/out: " + ex.getMessage());
        }
    }
    
    private void viewAttendance() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT work_date, shift_type, start_time, end_time, working_hours " +
                          "FROM attendance WHERE employee_id = ? ORDER BY work_date DESC, start_time DESC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            ResultSet rs = pst.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Date: ").append(rs.getDate("work_date"))
                  .append("\nShift: ").append(rs.getString("shift_type"))
                  .append("\nStart: ").append(rs.getTime("start_time"))
                  .append("\nEnd: ").append(rs.getTime("end_time") != null ? rs.getTime("end_time") : "Not clocked out")
                  .append("\nHours: ").append(rs.getDouble("working_hours") != 0 ? rs.getDouble("working_hours") : "In progress")
                  .append("\n\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Attendance Records", JOptionPane.INFORMATION_MESSAGE);
            statusBar.setText("Attendance records displayed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error viewing attendance: " + ex.getMessage());
        }
    }
    
    private void viewSalary() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT payment_date, amount FROM salary_payments WHERE employee_id = ? ORDER BY payment_date DESC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            ResultSet rs = pst.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Date: ").append(rs.getDate("payment_date"))
                  .append("\nAmount: ₹").append(rs.getDouble("amount"))
                  .append("\n\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Salary Records", JOptionPane.INFORMATION_MESSAGE);
            statusBar.setText("Salary records displayed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error viewing salary: " + ex.getMessage());
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setPreferredSize(new Dimension(250, 45));
        
        // Add hover effect
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EmployeeDashboard("EMP001").setVisible(true);
            }
        });
    }
}
