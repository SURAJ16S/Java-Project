package com.company.gui;

import com.company.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ApplicationForm extends JFrame {
    private JTextField fullNameField;
    private JTextField birthdateField; // format "yyyy-MM-dd"
    private JTextField workExpField;
    private JTextField emailField;
    private JTextField sectorField;
    private JComboBox<String> genderBox;
    
    public ApplicationForm() {
        setTitle("Job Application Form");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        
        panel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
        panel.add(fullNameField);
        
        panel.add(new JLabel("Birthdate (yyyy-MM-dd):"));
        birthdateField = new JTextField();
        panel.add(birthdateField);
        
        panel.add(new JLabel("Work Experience (years):"));
        workExpField = new JTextField();
        panel.add(workExpField);
        
        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);
        
        panel.add(new JLabel("Interested Sector:"));
        sectorField = new JTextField();
        panel.add(sectorField);
        
        panel.add(new JLabel("Gender:"));
        genderBox = new JComboBox<>(new String[]{"M", "F", "Other"});
        panel.add(genderBox);
        
        JButton submitBtn = new JButton("Submit Application");
        submitBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                submitApplication();
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
        bottomPanel.add(submitBtn);
        bottomPanel.add(backBtn);
        
        add(panel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void submitApplication(){
        String fullName = fullNameField.getText().trim();
        String birthdate = birthdateField.getText().trim();
        String workExp = workExpField.getText().trim();
        String email = emailField.getText().trim();
        String sector = sectorField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        
        if (fullName.isEmpty() || birthdate.isEmpty() || workExp.isEmpty() || email.isEmpty() || sector.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }
        
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO job_applications(full_name, birthdate, work_experience, email, interested_sector, gender, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, fullName);
            pst.setString(2, birthdate);
            pst.setInt(3, Integer.parseInt(workExp));
            pst.setString(4, email);
            pst.setString(5, sector);
            pst.setString(6, gender);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            new LoginFrame().setVisible(true);
            dispose();
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Submission error: " + ex.getMessage());
        }
    }
}
