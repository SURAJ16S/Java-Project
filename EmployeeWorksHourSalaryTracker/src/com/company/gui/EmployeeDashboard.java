package com.company.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EmployeeDashboard extends JFrame {
    private String empId;
    
    public EmployeeDashboard(String empId) {
        this.empId = empId;
        setTitle("Employee Dashboard - " + empId);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Welcome, Employee " + empId), BorderLayout.NORTH);
        JButton updateUpiBtn = new JButton("Update UPI");
        updateUpiBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String upi = JOptionPane.showInputDialog("Enter new UPI ID:");
                if(upi != null && !upi.trim().isEmpty()){
                    // Update UPI in database (stub)
                    JOptionPane.showMessageDialog(null, "UPI updated to: " + upi);
                }
            }
        });
        panel.add(updateUpiBtn, BorderLayout.CENTER);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        panel.add(logoutBtn, BorderLayout.SOUTH);
        add(panel);
    }
}
