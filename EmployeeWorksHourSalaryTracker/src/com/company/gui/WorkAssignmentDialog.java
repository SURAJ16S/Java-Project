package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkAssignmentDialog extends JDialog {
    private JTextField hoursField;
    private JTextField hourlyRateField;
    private JComboBox<String> workTypeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextArea descriptionArea;
    private JLabel statusLabel;
    private int applicationId;
    private JFrame parent;
    private String employeeIdFromEmployeeTab = null;
    
    public WorkAssignmentDialog(JFrame parent, int applicationId) {
        super(parent, "Assign Work", true);
        this.parent = parent;
        this.applicationId = applicationId;
        
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        initComponents();
        loadApplicationDetails();
    }
    
    public WorkAssignmentDialog(JFrame parent, String employeeId) {
        super(parent, "Assign Work", true);
        this.parent = parent;
        this.employeeIdFromEmployeeTab = employeeId;
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents();
        statusLabel.setText("Assigning work to Employee ID: " + employeeId);
    }
    
    private void initComponents() {
        // Main panel with a nice background
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
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 10);
        
        // Work Type
        JLabel workTypeLabel = new JLabel("Work Type:");
        workTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(workTypeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        workTypeCombo = new JComboBox<>(new String[]{"Full-time", "Part-time", "Contract", "Project-based"});
        workTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(workTypeCombo, gbc);
        
        // Hours
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hoursLabel = new JLabel("Hours per Week:");
        hoursLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(hoursLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        hoursField = new JTextField();
        hoursField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(hoursField, gbc);
        
        // Hourly Rate
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel rateLabel = new JLabel("Hourly Rate (₹):");
        rateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(rateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        hourlyRateField = new JTextField();
        hourlyRateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(hourlyRateField, gbc);
        
        // Start Date
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(startDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setValue(new Date());
        formPanel.add(startDateSpinner, gbc);
        
        // End Date
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(endDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3); // Default to 3 months
        endDateSpinner.setValue(cal.getTime());
        formPanel.add(endDateSpinner, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton cancelBtn = createStyledButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        JButton assignBtn = createStyledButton("Assign Work");
        assignBtn.setBackground(new Color(60, 179, 113)); // Green color
        assignBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                assignBtn.setBackground(new Color(50, 150, 90));
            }
            public void mouseExited(MouseEvent e) {
                assignBtn.setBackground(new Color(60, 179, 113));
            }
        });
        assignBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignWork();
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(assignBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadApplicationDetails() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT full_name, email, interested_sector FROM job_applications WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, applicationId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("full_name");
                String email = rs.getString("email");
                String sector = rs.getString("interested_sector");
                
                statusLabel.setText("Assigning work to: " + name + " (" + email + ") - " + sector);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error loading application details: " + ex.getMessage());
        }
    }
    
    private void assignWork() {
        // Validate inputs
        if (hoursField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter hours per week.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (hourlyRateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter hourly rate.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int hours = Integer.parseInt(hoursField.getText().trim());
            double hourlyRate = Double.parseDouble(hourlyRateField.getText().trim());
            
            if (hours <= 0) {
                JOptionPane.showMessageDialog(this, "Hours must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (hourlyRate <= 0) {
                JOptionPane.showMessageDialog(this, "Hourly rate must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();
            
            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Connection con = DBConnection.getConnection();
            String employeeId;
            int applicationIdForInsert = -1;
            if (employeeIdFromEmployeeTab != null) {
                // Use the provided employeeId directly
                employeeId = employeeIdFromEmployeeTab;
            } else {
                // Existing logic: get employeeId from applicationId
                String getEmailQuery = "SELECT email FROM job_applications WHERE application_id = ?";
                PreparedStatement getEmailStmt = con.prepareStatement(getEmailQuery);
                getEmailStmt.setInt(1, applicationId);
                ResultSet emailRs = getEmailStmt.executeQuery();
                String email = null;
                if (emailRs.next()) {
                    email = emailRs.getString("email");
                } else {
                    JOptionPane.showMessageDialog(this, "No email found for this application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String employeeQuery = "SELECT employee_id FROM employees WHERE email = ?";
                PreparedStatement employeeStmt = con.prepareStatement(employeeQuery);
                employeeStmt.setString(1, email);
                ResultSet employeeRs = employeeStmt.executeQuery();
                if (!employeeRs.next()) {
                    JOptionPane.showMessageDialog(this, "Employee record not found for this application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                employeeId = employeeRs.getString("employee_id");
                // Also get the application_id for the insert
                applicationIdForInsert = applicationId;
            }
            // If assigning from Employees tab, try to find the latest application_id for this employee
            if (employeeIdFromEmployeeTab != null) {
                String findAppIdQuery = "SELECT application_id FROM job_applications WHERE email = (SELECT email FROM employees WHERE employee_id = ?) ORDER BY application_id DESC LIMIT 1";
                PreparedStatement findAppIdStmt = con.prepareStatement(findAppIdQuery);
                findAppIdStmt.setString(1, employeeIdFromEmployeeTab);
                ResultSet appIdRs = findAppIdStmt.executeQuery();
                if (appIdRs.next()) {
                    applicationIdForInsert = appIdRs.getInt("application_id");
                }
                // If no job application found, create one automatically
                if (applicationIdForInsert == -1) {
                    // Get employee details
                    String empDetailsQuery = "SELECT full_name, email FROM employees WHERE employee_id = ?";
                    PreparedStatement empDetailsStmt = con.prepareStatement(empDetailsQuery);
                    empDetailsStmt.setString(1, employeeIdFromEmployeeTab);
                    ResultSet empDetailsRs = empDetailsStmt.executeQuery();
                    String fullName = null;
                    String email = null;
                    if (empDetailsRs.next()) {
                        fullName = empDetailsRs.getString("full_name");
                        email = empDetailsRs.getString("email");
                    }
                    // Insert minimal job application
                    if (fullName != null && email != null) {
                        String insertAppQuery = "INSERT INTO job_applications (full_name, birthdate, work_experience, profile_pic, resume, interested_sector, email, gender, status) VALUES (?, CURDATE(), 0, '', '', '', ?, 'Other', 'approved')";
                        PreparedStatement insertAppStmt = con.prepareStatement(insertAppQuery, Statement.RETURN_GENERATED_KEYS);
                        insertAppStmt.setString(1, fullName);
                        insertAppStmt.setString(2, email);
                        insertAppStmt.executeUpdate();
                        ResultSet genKeys = insertAppStmt.getGeneratedKeys();
                        if (genKeys.next()) {
                            applicationIdForInsert = genKeys.getInt(1);
                        }
                    }
                }
                // If still not found, show error
                if (applicationIdForInsert == -1) {
                    JOptionPane.showMessageDialog(this, "No job application found or could be created for this employee. Cannot assign work.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Insert work assignment
            String insertQuery = "INSERT INTO work_assignments (application_id, employee_id, work_type, hours, hourly_rate, start_date, end_date, description, status) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'active')";
            
            PreparedStatement insertStmt = con.prepareStatement(insertQuery);
            insertStmt.setInt(1, applicationIdForInsert);
            insertStmt.setString(2, employeeId);
            insertStmt.setString(3, (String) workTypeCombo.getSelectedItem());
            insertStmt.setInt(4, hours);
            insertStmt.setDouble(5, hourlyRate);
            insertStmt.setDate(6, new java.sql.Date(startDate.getTime()));
            insertStmt.setDate(7, new java.sql.Date(endDate.getTime()));
            insertStmt.setString(8, descriptionArea.getText().trim());
            
            insertStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, 
                "Work assigned successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for hours and hourly rate.", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error assigning work: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
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