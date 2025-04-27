package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.image.BufferedImage;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        
        JButton jobAllocationBtn = new JButton("Job Allocation");
        jobAllocationBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new JobAllocationPanel().setVisible(true);
                dispose();
            }
        });
        
        JButton viewEmployeesBtn = new JButton("Display/Search Employees");
        viewEmployeesBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                displayEmployees();
            }
        });
        
        JButton paySalaryBtn = new JButton("Pay Salary");
        paySalaryBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                paySalary();
            }
        });
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        
        panel.add(jobAllocationBtn);
        panel.add(viewEmployeesBtn);
        panel.add(paySalaryBtn);
        panel.add(logoutBtn);
        
        add(panel, BorderLayout.CENTER);
    }
    
    private void displayEmployees() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM employees";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            StringBuilder sb = new StringBuilder();
            while(rs.next()){
                sb.append("ID: ").append(rs.getString("employee_id"))
                  .append(", Name: ").append(rs.getString("full_name"))
                  .append(", Dept: ").append(rs.getString("department"))
                  .append(", Designation: ").append(rs.getString("designation"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Employee Details", JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying employees: " + ex.getMessage());
        }
    }
    
    private void paySalary() {
        String empId = JOptionPane.showInputDialog("Enter Employee ID for salary payment:");
        if (empId == null || empId.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Employee ID is required.");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            // For simplicity, assume salary calculation based on number of attendance records.
            String query = "SELECT COUNT(*) AS daysWorked FROM attendance WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, empId);
            ResultSet rs = pst.executeQuery();
            int daysWorked = 0;
            if(rs.next()){
                daysWorked = rs.getInt("daysWorked");
            }
            // Assume daily rate is ₹1000.
            double totalSalary = daysWorked * 1000;
            
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Total salary for " + empId + " is ₹" + totalSalary + ".\nProceed with payment?",
                    "Confirm Payment", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                String paymentQuery = "INSERT INTO salary_payments(employee_id, payment_date, amount) VALUES (?, CURDATE(), ?)";
                PreparedStatement pst2 = con.prepareStatement(paymentQuery);
                pst2.setString(1, empId);
                pst2.setDouble(2, totalSalary);
                pst2.executeUpdate();
                
                // Generate QR Code stub (using QRCodeGenerator).
                BufferedImage qr = com.company.utils.QRCodeGenerator.generateQRCode("Employee: " + empId + ", Salary: ₹" + totalSalary);
                JOptionPane.showMessageDialog(this, new ImageIcon(qr), "Salary Payment - QR Code", JOptionPane.PLAIN_MESSAGE);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during salary payment: " + ex.getMessage());
        }
    }
}
