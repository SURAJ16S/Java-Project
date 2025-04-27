package com.company.gui;

import com.company.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class ApplicationForm extends JFrame {
    private JTextField fullNameField;
    private JTextField dobField;
    private JTextField workExpField;
    private JTextField emailField;
    private JComboBox<String> sectorCombo;
    private JComboBox<String> genderCombo;
    private JLabel imageLabel;
    private JTextField resumeField; // displays selected resume file path
    
    private String imagePath = "";
    private String resumePath = "";
    
    public ApplicationForm(){
        setTitle("Job Application Form");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        
        panel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
        panel.add(fullNameField);
        
        panel.add(new JLabel("Date of Birth (yyyy-MM-dd):"));
        dobField = new JTextField();
        panel.add(dobField);
        
        panel.add(new JLabel("Work Experience (years):"));
        workExpField = new JTextField();
        panel.add(workExpField);
        
        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);
        
        panel.add(new JLabel("Interested Sector:"));
        String[] sectors = {"IT", "HR", "Finance", "Marketing", "Other"};
        sectorCombo = new JComboBox<>(sectors);
        panel.add(sectorCombo);
        
        panel.add(new JLabel("Gender:"));
        String[] genders = {"Male", "Female", "Other"};
        genderCombo = new JComboBox<>(genders);
        panel.add(genderCombo);
        
        JButton uploadImageBtn = new JButton("Upload Image");
        uploadImageBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int res = fc.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();
                    imagePath = file.getAbsolutePath();
                    // Display a scaled image in the label
                    ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                    imageLabel.setIcon(icon);
                }
            }
        });
        panel.add(uploadImageBtn);
        
        imageLabel = new JLabel("No Image Selected");
        panel.add(imageLabel);
        
        JButton uploadResumeBtn = new JButton("Upload Resume");
        uploadResumeBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int res = fc.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();
                    resumePath = file.getAbsolutePath();
                    resumeField.setText(resumePath);
                }
            }
        });
        panel.add(uploadResumeBtn);
        
        resumeField = new JTextField();
        resumeField.setEditable(false);
        panel.add(resumeField);
        
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
        String dob = dobField.getText().trim();
        String workExp = workExpField.getText().trim();
        String email = emailField.getText().trim();
        String sector = (String)sectorCombo.getSelectedItem();
        String gender = (String)genderCombo.getSelectedItem();
        
        if(fullName.isEmpty() || dob.isEmpty() || workExp.isEmpty() || email.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill all mandatory fields.");
            return;
        }
        
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO job_applications(full_name, birthdate, work_experience, email, interested_sector, gender, profile_pic, resume, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'pending')";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, fullName);
            pst.setString(2, dob);
            pst.setInt(3, Integer.parseInt(workExp));
            pst.setString(4, email);
            pst.setString(5, sector);
            pst.setString(6, gender);
            pst.setString(7, imagePath);
            pst.setString(8, resumePath);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            new LoginFrame().setVisible(true);
            dispose();
        } catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting application: " + ex.getMessage());
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
                new ApplicationForm().setVisible(true);
            }
        });
    }
}
