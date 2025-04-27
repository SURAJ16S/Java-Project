package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class WorkAssignmentDialog extends JDialog {
    private JLabel statusLabel;
    private int applicationId;
    private JComboBox<String> workTypeCombo;
    private JTextField hourlyRateField;
    private JTextField hoursField;
    private JTextField startDateField;
    private JTextField endDateField;
    
    public WorkAssignmentDialog(JFrame parent, int applicationId) {
        super(parent, "Assign Work", true);
        this.applicationId = applicationId;
        initComponents();
        loadApplicationDetails();
    }
    
    private void initComponents() {
        setSize(500, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Work Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Work Type:"), gbc);
        
        gbc.gridx = 1;
        workTypeCombo = new JComboBox<>(new String[]{"fulltime", "nighttime", "extra", "hourly"});
        workTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsVisibility();
            }
        });
        formPanel.add(workTypeCombo, gbc);
        
        // Hourly Rate
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Hourly Rate (₹):"), gbc);
        
        gbc.gridx = 1;
        hourlyRateField = new JTextField(15);
        formPanel.add(hourlyRateField, gbc);
        
        // Hours
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Hours:"), gbc);
        
        gbc.gridx = 1;
        hoursField = new JTextField(15);
        formPanel.add(hoursField, gbc);
        
        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Start Date:"), gbc);
        
        gbc.gridx = 1;
        startDateField = new JTextField(15);
        startDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        formPanel.add(startDateField, gbc);
        
        // End Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("End Date:"), gbc);
        
        gbc.gridx = 1;
        endDateField = new JTextField(15);
        formPanel.add(endDateField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton assignBtn = createStyledButton("Assign Work");
        assignBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignWork();
            }
        });
        
        JButton cancelBtn = createStyledButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(assignBtn);
        buttonPanel.add(cancelBtn);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Initial field visibility update
        updateFieldsVisibility();
    }
    
    private void updateFieldsVisibility() {
        String selectedType = (String) workTypeCombo.getSelectedItem();
        boolean isHourly = "hourly".equals(selectedType);
        boolean isExtra = "extra".equals(selectedType);
        
        hourlyRateField.setEnabled(isHourly || isExtra);
        hoursField.setEnabled(isHourly || isExtra);
    }
    
    private void loadApplicationDetails() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM job_applications WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, applicationId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Set default hourly rate based on sector
                String sector = rs.getString("interested_sector");
                double defaultRate = getDefaultHourlyRate(sector);
                hourlyRateField.setText(String.valueOf(defaultRate));
                
                // Set default hours based on work type
                hoursField.setText("8"); // Default 8 hours
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error loading application details: " + ex.getMessage());
        }
    }
    
    private double getDefaultHourlyRate(String sector) {
        switch (sector.toLowerCase()) {
            case "development":
                return 500.0;
            case "design":
                return 450.0;
            case "testing":
                return 400.0;
            case "management":
                return 600.0;
            default:
                return 400.0;
        }
    }
    
    private void assignWork() {
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }
            
            Connection con = DBConnection.getConnection();
            
            // Get employee ID from application
            String employeeQuery = "SELECT employee_id FROM employees WHERE application_id = ?";
            PreparedStatement empStmt = con.prepareStatement(employeeQuery);
            empStmt.setInt(1, applicationId);
            ResultSet empRs = empStmt.executeQuery();
            
            if (!empRs.next()) {
                statusLabel.setText("Error: No employee found for this application.");
                return;
            }
            
            String employeeId = empRs.getString("employee_id");
            
            // Insert work assignment
            String insertQuery = "INSERT INTO work_assignments (application_id, employee_id, work_type, " +
                               "hours, hourly_rate, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";
            PreparedStatement pst = con.prepareStatement(insertQuery);
            pst.setInt(1, applicationId);
            pst.setString(2, employeeId);
            pst.setString(3, (String) workTypeCombo.getSelectedItem());
            pst.setDouble(4, Double.parseDouble(hoursField.getText()));
            pst.setDouble(5, Double.parseDouble(hourlyRateField.getText()));
            pst.setDate(6, java.sql.Date.valueOf(startDateField.getText()));
            pst.setDate(7, endDateField.getText().isEmpty() ? null : java.sql.Date.valueOf(endDateField.getText()));
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, 
                "Work assigned successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error assigning work: " + ex.getMessage());
        }
    }
    
    private boolean validateInputs() {
        try {
            // Validate work type
            if (workTypeCombo.getSelectedItem() == null) {
                statusLabel.setText("Please select a work type.");
                return false;
            }
            
            // Validate hourly rate and hours for hourly/extra work
            String selectedType = (String) workTypeCombo.getSelectedItem();
            if ("hourly".equals(selectedType) || "extra".equals(selectedType)) {
                if (hourlyRateField.getText().isEmpty()) {
                    statusLabel.setText("Please enter hourly rate.");
                    return false;
                }
                if (hoursField.getText().isEmpty()) {
                    statusLabel.setText("Please enter hours.");
                    return false;
                }
                try {
                    double rate = Double.parseDouble(hourlyRateField.getText());
                    double hours = Double.parseDouble(hoursField.getText());
                    if (rate <= 0 || hours <= 0) {
                        statusLabel.setText("Hourly rate and hours must be greater than 0.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    statusLabel.setText("Invalid number format for rate or hours.");
                    return false;
                }
            }
            
            // Validate dates
            try {
                LocalDate.parse(startDateField.getText());
                if (!endDateField.getText().isEmpty()) {
                    LocalDate.parse(endDateField.getText());
                }
            } catch (Exception e) {
                statusLabel.setText("Invalid date format. Use YYYY-MM-DD.");
                return false;
            }
            
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error validating inputs: " + ex.getMessage());
            return false;
        }
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
} 