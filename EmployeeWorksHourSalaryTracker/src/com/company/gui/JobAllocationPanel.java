package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class JobAllocationPanel extends JFrame {
    private JComboBox<String> applicantCombo;
    
    public JobAllocationPanel(){
        setTitle("Job Allocation");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Applicant:"));
        applicantCombo = new JComboBox<>();
        populateApplicants();
        topPanel.add(applicantCombo);
        panel.add(topPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton allocateBtn = new JButton("Allocate Job");
        allocateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                allocateJob();
            }
        });
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new AdminDashboard().setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(allocateBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    private void populateApplicants(){
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT application_id, full_name FROM job_applications WHERE status = 'pending'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                String item = rs.getString("full_name") + " - ID:" + rs.getInt("application_id");
                applicantCombo.addItem(item);
            }
        } catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching applications: " + ex.getMessage());
        }
    }
    
    private void allocateJob(){
        String selected = (String) applicantCombo.getSelectedItem();
        if(selected == null || selected.isEmpty()){
            JOptionPane.showMessageDialog(this, "No applicant selected.");
            return;
        }
        try {
            // Extract the application ID from the selected string.
            String[] parts = selected.split("- ID:");
            int appId = Integer.parseInt(parts[1].trim());
            
            Connection con = DBConnection.getConnection();
            String query = "UPDATE job_applications SET status='approved' WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, appId);
            int rows = pst.executeUpdate();
            if(rows > 0){
                JOptionPane.showMessageDialog(this, "Job allocated successfully for application ID " + appId);
                applicantCombo.removeItem(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Allocation failed.");
            }
        } catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during allocation: " + ex.getMessage());
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
                new JobAllocationPanel().setVisible(true);
            }
        });
    }
}
