package com.company.gui;

import com.company.database.DBConnection;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.DefaultCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.math.BigDecimal;
import java.sql.Time;

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
            String query = "SELECT full_name, upi, mobile_number FROM employees WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JLabel welcomeLabel = new JLabel("Welcome, " + rs.getString("full_name"));
                welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                welcomeLabel.setForeground(new Color(70, 70, 70));
                infoPanel.add(welcomeLabel);
                
                // Add UPI ID and mobile number info if exists
                String upi = rs.getString("upi");
                String mobile = rs.getString("mobile_number");
                if (upi != null && !upi.isEmpty() && mobile != null && !mobile.isEmpty()) {
                    infoPanel.add(Box.createHorizontalStrut(20));
                    JLabel upiLabel = new JLabel("UPI ID: " + upi);
                    upiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    upiLabel.setForeground(new Color(70, 70, 70));
                    infoPanel.add(upiLabel);
                    
                    infoPanel.add(Box.createHorizontalStrut(20));
                    JLabel mobileLabel = new JLabel("Mobile: " + mobile);
                    mobileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    mobileLabel.setForeground(new Color(70, 70, 70));
                    infoPanel.add(mobileLabel);
                }
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
        
        // Set UPI ID and Mobile Number button
        JButton upiBtn = createStyledButton("Set UPI & Mobile");
        upiBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUpiAndMobile();
            }
        });
        buttonPanel.add(upiBtn, gbc);
        
        // Display QR Code button
        gbc.gridy = 1;
        JButton qrBtn = createStyledButton("Display QR Code");
        qrBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayQRCode();
            }
        });
        buttonPanel.add(qrBtn, gbc);
        
        // View Attendance button
        gbc.gridy = 2;
        JButton attendanceBtn = createStyledButton("View Attendance");
        attendanceBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAttendance();
            }
        });
        buttonPanel.add(attendanceBtn, gbc);
        
        // View Salary button
        gbc.gridy = 3;
        JButton salaryBtn = createStyledButton("View Salary");
        salaryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewSalary();
            }
        });
        buttonPanel.add(salaryBtn, gbc);
        
        // Logout button
        gbc.gridy = 4;
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
        JDialog dialog = new JDialog(this, "View Attendance", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table model with proper column names
        String[] columnNames = {"Date", "Day", "Shift Type", "Start Time", "End Time", "Working Hours", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Day
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Shift Type
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Time
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // End Time
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Working Hours
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        // Set custom renderer for Status column
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Add month selector at the top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(240, 240, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JComboBox<String> monthBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthBox.addActionListener(e -> {
            int selectedMonth = monthBox.getSelectedIndex() + 1;
            loadAttendanceData(model, selectedMonth);
        });

        topPanel.add(new JLabel("Select Month: "));
        topPanel.add(monthBox);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);

        // Load attendance data
        loadAttendanceData(model, LocalDate.now().getMonthValue());
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
    
    private void setUpiAndMobile() {
        try {
            Connection con = DBConnection.getConnection();
            
            // Get current UPI ID and mobile number if exists
            String currentUpi = null;
            String currentMobile = null;
            String getQuery = "SELECT upi, mobile_number FROM employees WHERE employee_id = ?";
            PreparedStatement getStmt = con.prepareStatement(getQuery);
            getStmt.setString(1, employeeId);
            ResultSet rs = getStmt.executeQuery();
            if (rs.next()) {
                currentUpi = rs.getString("upi");
                currentMobile = rs.getString("mobile_number");
            }
            
            // Create dialog for UPI ID and mobile number input
            JDialog dialog = new JDialog(this, "Set UPI ID and Mobile Number", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(240, 240, 245));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Current UPI ID
            JLabel currentUpiLabel = new JLabel("Current UPI ID:");
            currentUpiLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            panel.add(currentUpiLabel, gbc);
            
            gbc.gridx = 1;
            JLabel currentUpiValue = new JLabel(currentUpi != null ? currentUpi : "Not set");
            currentUpiValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel.add(currentUpiValue, gbc);
            
            // New UPI ID
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel newUpiLabel = new JLabel("New UPI ID:");
            newUpiLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            panel.add(newUpiLabel, gbc);
            
            gbc.gridx = 1;
            JTextField upiField = new JTextField(20);
            upiField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel.add(upiField, gbc);
            
            // Current Mobile Number
            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel currentMobileLabel = new JLabel("Current Mobile:");
            currentMobileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            panel.add(currentMobileLabel, gbc);
            
            gbc.gridx = 1;
            JLabel currentMobileValue = new JLabel(currentMobile != null ? currentMobile : "Not set");
            currentMobileValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel.add(currentMobileValue, gbc);
            
            // New Mobile Number
            gbc.gridx = 0;
            gbc.gridy = 3;
            JLabel newMobileLabel = new JLabel("New Mobile:");
            newMobileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            panel.add(newMobileLabel, gbc);
            
            gbc.gridx = 1;
            JTextField mobileField = new JTextField(20);
            mobileField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panel.add(mobileField, gbc);
            
            // Save button
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            
            JButton saveBtn = createStyledButton("Save");
            saveBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String newUpi = upiField.getText().trim();
                    String newMobile = mobileField.getText().trim();
                    
                    if (newUpi.isEmpty() || newMobile.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please enter both UPI ID and Mobile Number", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    try {
                        String updateQuery = "UPDATE employees SET upi = ?, mobile_number = ? WHERE employee_id = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                        updateStmt.setString(1, newUpi);
                        updateStmt.setString(2, newMobile);
                        updateStmt.setString(3, employeeId);
                        updateStmt.executeUpdate();
                        
                        statusBar.setText("UPI ID and Mobile Number updated successfully");
                        dialog.dispose();
                        // Refresh the dashboard to show updated information
                        dispose();
                        new EmployeeDashboard(employeeId).setVisible(true);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, "Error updating information: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            panel.add(saveBtn, gbc);
            
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error setting UPI ID and Mobile Number: " + ex.getMessage());
        }
    }
    
    private void displayQRCode() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT e.upi, e.full_name, e.employee_id, e.department, e.designation " +
                          "FROM employees e WHERE e.employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String upi = rs.getString("upi");
                String fullName = rs.getString("full_name");
                String empId = rs.getString("employee_id");
                String department = rs.getString("department");
                String designation = rs.getString("designation");
                
                if (upi == null || upi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please set your UPI ID first using the 'Set UPI & Mobile' option", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create a more detailed UPI string for salary payments
                String upiString = String.format("upi://pay?pa=%s&pn=%s&am=&tn=Salary%%20Payment%%20-%s%%20(%s)&cu=INR", 
                    upi, 
                    fullName,
                    empId,
                    department);
                
                // Generate QR code
                BitMatrix bitMatrix = new MultiFormatWriter().encode(upiString, BarcodeFormat.QR_CODE, 300, 300);
                File qrFile = new File("salary_qr_" + empId + ".png");
                MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get(qrFile.getAbsolutePath()));
                
                // Show QR code in dialog
                JDialog qrDialog = new JDialog(this, "Salary Payment QR Code", true);
                qrDialog.setSize(400, 500);
                qrDialog.setLocationRelativeTo(this);
                
                JPanel qrPanel = new JPanel(new BorderLayout());
                qrPanel.setBackground(new Color(240, 240, 245));
                qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                // Add employee details
                JPanel detailsPanel = new JPanel(new GridBagLayout());
                detailsPanel.setBackground(new Color(240, 240, 245));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(5, 5, 5, 5);
                
                JLabel nameLabel = new JLabel("Employee: " + fullName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                detailsPanel.add(nameLabel, gbc);
                
                gbc.gridy = 1;
                JLabel idLabel = new JLabel("ID: " + empId);
                idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                detailsPanel.add(idLabel, gbc);
                
                gbc.gridy = 2;
                JLabel deptLabel = new JLabel("Department: " + department);
                deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                detailsPanel.add(deptLabel, gbc);
                
                gbc.gridy = 3;
                JLabel desigLabel = new JLabel("Designation: " + designation);
                desigLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                detailsPanel.add(desigLabel, gbc);
                
                qrPanel.add(detailsPanel, BorderLayout.NORTH);
                
                // Add QR code
                ImageIcon qrIcon = new ImageIcon(qrFile.getAbsolutePath());
                JLabel qrLabel = new JLabel(qrIcon);
                qrLabel.setHorizontalAlignment(JLabel.CENTER);
                qrPanel.add(qrLabel, BorderLayout.CENTER);
                
                // Add UPI string for verification
                JTextArea upiStringArea = new JTextArea("UPI String:\n" + upiString);
                upiStringArea.setEditable(false);
                upiStringArea.setBackground(new Color(240, 240, 245));
                upiStringArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                qrPanel.add(upiStringArea, BorderLayout.SOUTH);
                
                JButton closeBtn = createStyledButton("Close");
                closeBtn.addActionListener(e -> qrDialog.dispose());
                qrPanel.add(closeBtn, BorderLayout.SOUTH);
                
                qrDialog.add(qrPanel);
                qrDialog.setVisible(true);
                
                statusBar.setText("Salary Payment QR Code displayed successfully");
            }
        } catch (SQLException | WriterException | IOException ex) {
            ex.printStackTrace();
            statusBar.setText("Error displaying QR Code: " + ex.getMessage());
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

    private void loadAttendanceData(DefaultTableModel model, int month) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            
            // Get current year
            int currentYear = LocalDate.now().getYear();
            
            // Get all dates in the selected month
            LocalDate firstDay = LocalDate.of(currentYear, month, 1);
            int daysInMonth = firstDay.lengthOfMonth();
            
            // Define public holidays
            Set<LocalDate> publicHolidays = new HashSet<>();
            publicHolidays.add(LocalDate.of(currentYear, 1, 1)); // New Year
            publicHolidays.add(LocalDate.of(currentYear, 1, 26)); // Republic Day
            publicHolidays.add(LocalDate.of(currentYear, 8, 15)); // Independence Day
            publicHolidays.add(LocalDate.of(currentYear, 10, 2)); // Gandhi Jayanti
            
            // Get existing attendance records
            String query = "SELECT work_date, shift_type, start_time, end_time, working_hours, attendance_status " +
                          "FROM attendance WHERE employee_id = ? AND " +
                          "MONTH(work_date) = ? AND YEAR(work_date) = ? " +
                          "ORDER BY work_date";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            pst.setInt(2, month);
            pst.setInt(3, currentYear);
            ResultSet rs = pst.executeQuery();
            
            // Create a map of dates to attendance records
            Map<LocalDate, AttendanceRecord> attendanceMap = new HashMap<>();
            while (rs.next()) {
                Date workDate = rs.getDate("work_date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                BigDecimal workingHours = rs.getBigDecimal("working_hours");
                
                // Format times to 12-hour format
                String formattedStartTime = startTime != null ? 
                    String.format("%02d:%02d %s", 
                        (startTime.getHours() % 12 == 0 ? 12 : startTime.getHours() % 12),
                        startTime.getMinutes(),
                        startTime.getHours() >= 12 ? "PM" : "AM") : null;
                
                String formattedEndTime = endTime != null ? 
                    String.format("%02d:%02d %s", 
                        (endTime.getHours() % 12 == 0 ? 12 : endTime.getHours() % 12),
                        endTime.getMinutes(),
                        endTime.getHours() >= 12 ? "PM" : "AM") : null;
                
                AttendanceRecord record = new AttendanceRecord(
                    rs.getString("shift_type"),
                    formattedStartTime,
                    formattedEndTime,
                    workingHours,
                    rs.getString("attendance_status")
                );
                attendanceMap.put(workDate.toLocalDate(), record);
            }
            
            // Add all dates to the table
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(currentYear, month, day);
                AttendanceRecord record = attendanceMap.get(date);
                
                if (publicHolidays.contains(date)) {
                    model.addRow(new Object[]{
                        date,
                        date.getDayOfWeek().toString(),
                        "Holiday",
                        null,
                        null,
                        null,
                        "Holiday"
                    });
                } else if (date.getDayOfWeek().getValue() == 7) { // Sunday
                    model.addRow(new Object[]{
                        date,
                        date.getDayOfWeek().toString(),
                        "Sunday",
                        null,
                        null,
                        null,
                        "Sunday"
                    });
                } else if (record != null) {
                    model.addRow(new Object[]{
                        date,
                        date.getDayOfWeek().toString(),
                        record.shiftType,
                        record.startTime,
                        record.endTime,
                        record.workingHours != null ? String.format("%.2f", record.workingHours) : null,
                        record.status
                    });
                } else {
                    model.addRow(new Object[]{
                        date,
                        date.getDayOfWeek().toString(),
                        null,
                        null,
                        null,
                        null,
                        "Not Marked"
                    });
                }
            }
            
            // Update status bar with simple message
            statusBar.setText("Attendance records displayed for " + firstDay.getMonth().toString() + " " + currentYear);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading attendance data: " + ex.getMessage());
        }
    }

    private class AttendanceRecord {
        String shiftType;
        String startTime;  // Changed from Time to String for formatted time
        String endTime;    // Changed from Time to String for formatted time
        BigDecimal workingHours;
        String status;

        public AttendanceRecord(String shiftType, String startTime, String endTime, BigDecimal workingHours, String status) {
            this.shiftType = shiftType;
            this.startTime = startTime;
            this.endTime = endTime;
            this.workingHours = workingHours;
            this.status = status;
        }
    }

    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            
            if (status == null) {
                status = "Not Marked";
            }
            
            // Set default text color and background
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            switch (status) {
                case "Present":
                    label.setBackground(new Color(60, 179, 113)); // Bright Green
                    label.setBorder(BorderFactory.createLineBorder(new Color(40, 140, 80), 1));
                    break;
                case "Absent":
                    label.setBackground(new Color(220, 53, 69)); // Bright Red
                    label.setBorder(BorderFactory.createLineBorder(new Color(180, 30, 50), 1));
                    break;
                case "Holiday":
                    label.setBackground(new Color(255, 193, 7)); // Yellow
                    label.setForeground(Color.BLACK);
                    label.setBorder(BorderFactory.createLineBorder(new Color(220, 170, 0), 1));
                    break;
                case "Sunday":
                    label.setBackground(new Color(220, 53, 69)); // Red
                    label.setBorder(BorderFactory.createLineBorder(new Color(180, 30, 50), 1));
                    break;
                case "Not Marked":
                    label.setBackground(new Color(200, 200, 200)); // Gray
                    label.setForeground(Color.BLACK);
                    label.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
                    break;
                default:
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
            }
            
            label.setOpaque(true);
            return label;
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
                new EmployeeDashboard("EMP001").setVisible(true);
            }
        });
    }
}
