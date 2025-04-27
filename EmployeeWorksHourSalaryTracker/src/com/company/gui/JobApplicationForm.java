package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class JobApplicationForm extends JFrame {
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField birthdateField;
    private JTextField experienceField;
    private JComboBox<String> genderBox;
    private JComboBox<String> sectorBox;
    private JLabel profilePicLabel;
    private JLabel resumeLabel;
    private File selectedProfilePic;
    private File selectedResume;
    private JLabel statusLabel;
    
    public JobApplicationForm() {
        setTitle("Job Application");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents() {
        // Main panel with a nice background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Job Application Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel with a white background
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        
        // Full Name
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(fullNameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fullNameField = new JTextField();
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(emailField, gbc);
        
        // Birthdate
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel birthdateLabel = new JLabel("Birthdate (YYYY-MM-DD):");
        birthdateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(birthdateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        birthdateField = new JTextField();
        birthdateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(birthdateField, gbc);
        
        // Work Experience
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel experienceLabel = new JLabel("Work Experience (years):");
        experienceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(experienceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        experienceField = new JTextField();
        experienceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(experienceField, gbc);
        
        // Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(genderLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        genderBox = new JComboBox<>(new String[]{"M", "F", "Other"});
        genderBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(genderBox, gbc);
        
        // Interested Sector
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        JLabel sectorLabel = new JLabel("Interested Sector:");
        sectorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(sectorLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        sectorBox = new JComboBox<>(new String[]{"IT", "Finance", "HR", "Marketing", "Operations", "Sales"});
        sectorBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(sectorBox, gbc);
        
        // Profile Picture
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        JLabel profilePicLabel = new JLabel("Profile Picture:");
        profilePicLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(profilePicLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel profilePicPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profilePicPanel.setBackground(Color.WHITE);
        this.profilePicLabel = new JLabel("No file selected");
        this.profilePicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton browseProfileBtn = new JButton("Browse");
        browseProfileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        browseProfileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectProfilePicture();
            }
        });
        profilePicPanel.add(this.profilePicLabel);
        profilePicPanel.add(browseProfileBtn);
        formPanel.add(profilePicPanel, gbc);
        
        // Resume
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        JLabel resumeLabel = new JLabel("Resume:");
        resumeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(resumeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel resumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resumePanel.setBackground(Color.WHITE);
        this.resumeLabel = new JLabel("No file selected");
        this.resumeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton browseResumeBtn = new JButton("Browse");
        browseResumeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        browseResumeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectResume();
            }
        });
        resumePanel.add(this.resumeLabel);
        resumePanel.add(browseResumeBtn);
        formPanel.add(resumePanel, gbc);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 70, 70));
        formPanel.add(statusLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton submitBtn = createStyledButton("Submit Application");
        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                submitApplication();
            }
        });
        
        JButton backBtn = createStyledButton("Back to Login");
        backBtn.setBackground(new Color(180, 70, 70));
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(160, 60, 60));
            }
            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(new Color(180, 70, 70));
            }
        });
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void selectProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") 
                    || f.getName().toLowerCase().endsWith(".jpeg") 
                    || f.getName().toLowerCase().endsWith(".png");
            }
            public String getDescription() {
                return "Image files (*.jpg, *.jpeg, *.png)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedProfilePic = fileChooser.getSelectedFile();
            profilePicLabel.setText(selectedProfilePic.getName());
        }
    }
    
    private void selectResume() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf") 
                    || f.getName().toLowerCase().endsWith(".doc") 
                    || f.getName().toLowerCase().endsWith(".docx");
            }
            public String getDescription() {
                return "Document files (*.pdf, *.doc, *.docx)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedResume = fileChooser.getSelectedFile();
            resumeLabel.setText(selectedResume.getName());
        }
    }
    
    private void submitApplication() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String birthdate = birthdateField.getText().trim();
        String experience = experienceField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String sector = (String) sectorBox.getSelectedItem();
        
        // Validation
        if (fullName.isEmpty() || email.isEmpty() || birthdate.isEmpty() || experience.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }
        
        if (!isValidEmail(email)) {
            statusLabel.setText("Invalid email format.");
            return;
        }
        
        try {
            int exp = Integer.parseInt(experience);
            if (exp < 0) {
                statusLabel.setText("Work experience cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Work experience must be a number.");
            return;
        }
        
        if (selectedProfilePic == null || selectedResume == null) {
            statusLabel.setText("Please select both profile picture and resume.");
            return;
        }
        
        try {
            // Create uploads directory if it doesn't exist
            File uploadsDir = new File("uploads");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdir();
            }
            
            // Copy files to uploads directory
            String profilePicPath = "uploads/" + System.currentTimeMillis() + "_" + selectedProfilePic.getName();
            String resumePath = "uploads/" + System.currentTimeMillis() + "_" + selectedResume.getName();
            
            Files.copy(selectedProfilePic.toPath(), Paths.get(profilePicPath), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(selectedResume.toPath(), Paths.get(resumePath), StandardCopyOption.REPLACE_EXISTING);
            
            // Save to database
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO job_applications (full_name, birthdate, work_experience, profile_pic, resume, interested_sector, email, gender) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, fullName);
            pst.setString(2, birthdate);
            pst.setInt(3, Integer.parseInt(experience));
            pst.setString(4, profilePicPath);
            pst.setString(5, resumePath);
            pst.setString(6, sector);
            pst.setString(7, email);
            pst.setString(8, gender);
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Application submitted successfully!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error submitting application: " + ex.getMessage());
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
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
                new JobApplicationForm().setVisible(true);
            }
        });
    }
} 