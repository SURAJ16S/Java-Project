package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JobAllocationPanel extends JFrame {
    private JComboBox<String> applicantList;
    
    public JobAllocationPanel() {
        setTitle("Job Allocation");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Container for listing applicants
        JPanel listPanel = new JPanel(new FlowLayout());
        listPanel.add(new JLabel("Select Applicant:"));
        applicantList = new JComboBox<>();
        populateApplicants();
        listPanel.add(applicantList);
        panel.add(listPanel, BorderLayout.CENTER);
        
        // Buttons: Allocate and Back
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton allocateBtn = new JButton("Allocate Job");
        allocateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                allocateJob();
            }
        });
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(allocateBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    private void populateApplicants() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT application_id, full_name FROM job_applications WHERE status = 'pending'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                String item = rs.getString("full_name") + " - ID:" + rs.getInt("application_id");
                applicantList.addItem(item);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching applications: " + ex.getMessage());
        }
    }
    
    private void allocateJob() {
        String selected = (String) applicantList.getSelectedItem();
        if (selected == null || selected.isEmpty()){
            JOptionPane.showMessageDialog(this, "No applicant selected.");
            return;
        }
        try {
            // Extract Application ID from the selected item.
            String[] parts = selected.split("- ID:");
            int appId = Integer.parseInt(parts[1].trim());
            
            Connection con = DBConnection.getConnection();
            // Update the application status to "approved"
            String query = "UPDATE job_applications SET status='approved' WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, appId);
            int rows = pst.executeUpdate();
            if(rows > 0) {
                JOptionPane.showMessageDialog(this, "Job allocated successfully for application ID " + appId);
            } else {
                JOptionPane.showMessageDialog(this, "Allocation failed.");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error in allocation: " + ex.getMessage());
        }
    }
}
