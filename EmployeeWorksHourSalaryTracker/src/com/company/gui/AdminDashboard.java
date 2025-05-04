package com.company.gui;

import com.company.database.DBConnection;
import com.company.utils.QRCodeGenerator;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.io.File;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class AdminDashboard extends JFrame {
  
    // A status label at the bottom serves as our status bar.
    private JLabel statusBar;
    private JTabbedPane tabbedPane;
    private JTable employeeTable;
    private DefaultTableModel employeeTableModel;
    private String selectedEmployeeId;
    private int currentMonth;
    private int currentYear;
    private JPanel calendarGrid;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Initialize current month and year
        LocalDate today = LocalDate.now();
        currentMonth = today.getMonthValue();
        currentYear = today.getYear();
        
        initComponents();
    }

    private void initComponents() {
        // Main panel with a nice background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        // Add Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(180, 70, 70));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(new Color(160, 60, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(new Color(180, 70, 70));
            }
        });
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setBackground(new Color(240, 240, 245));
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Initialize status bar first
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(new Color(100, 100, 100));
        
        // Add tabs
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Job Applications", createJobApplicationsPanel());
        tabbedPane.addTab("Employees", createEmployeesPanel());
        tabbedPane.addTab("Attendance", createAttendancePanel());
        tabbedPane.addTab("Salary", createSalaryPanel());
        tabbedPane.addTab("Pay Salary", createPaySalaryPanel());
        tabbedPane.addTab("Salary History", createSalaryHistoryPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(230, 230, 230));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusPanel.add(statusBar, BorderLayout.WEST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Initialize employee table model
        String[] employeeColumns = {"Employee ID", "Name", "Department", "Designation", "Status"};
        employeeTableModel = new DefaultTableModel(employeeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(employeeTableModel);
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout(10, 10));
        homePanel.setBackground(new Color(240, 240, 245));
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Quick stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(new Color(240, 240, 245));
        
        // Create stat cards
        JPanel pendingAppsCard = createStatCard("Pending Applications", "0", new Color(70, 130, 180));
        JPanel totalEmployeesCard = createStatCard("Total Employees", "0", new Color(60, 179, 113));
        JPanel activeAssignmentsCard = createStatCard("Active Assignments", "0", new Color(255, 140, 0));
        JPanel totalSalaryCard = createStatCard("Total Salary (This Month)", "₹0", new Color(147, 112, 219));
        
        statsPanel.add(pendingAppsCard);
        statsPanel.add(totalEmployeesCard);
        statsPanel.add(activeAssignmentsCard);
        statsPanel.add(totalSalaryCard);
        
        // Center panel with welcome and stats
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 240, 245));
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(statsPanel);
        
        homePanel.add(centerPanel, BorderLayout.CENTER);
        
        // Update stats
        updateStats(pendingAppsCard, totalEmployeesCard, activeAssignmentsCard, totalSalaryCard);
        
        return homePanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void updateStats(JPanel pendingAppsCard, JPanel totalEmployeesCard, 
                            JPanel activeAssignmentsCard, JPanel totalSalaryCard) {
        try {
            Connection con = DBConnection.getConnection();
            
            // Get pending applications count
            String pendingQuery = "SELECT COUNT(*) FROM job_applications WHERE status = 'pending'";
            PreparedStatement pendingStmt = con.prepareStatement(pendingQuery);
            ResultSet pendingRs = pendingStmt.executeQuery();
            if (pendingRs.next()) {
                ((JLabel)pendingAppsCard.getComponent(1)).setText(String.valueOf(pendingRs.getInt(1)));
            }
            
            // Get total employees count
            String employeesQuery = "SELECT COUNT(*) FROM employees";
            PreparedStatement employeesStmt = con.prepareStatement(employeesQuery);
            ResultSet employeesRs = employeesStmt.executeQuery();
            if (employeesRs.next()) {
                ((JLabel)totalEmployeesCard.getComponent(1)).setText(String.valueOf(employeesRs.getInt(1)));
            }
            
            // Get active assignments count
            String assignmentsQuery = "SELECT COUNT(*) FROM work_assignments WHERE status = 'active'";
            PreparedStatement assignmentsStmt = con.prepareStatement(assignmentsQuery);
            ResultSet assignmentsRs = assignmentsStmt.executeQuery();
            if (assignmentsRs.next()) {
                ((JLabel)activeAssignmentsCard.getComponent(1)).setText(String.valueOf(assignmentsRs.getInt(1)));
            }
            
            // Get total salary for current month
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            int currentYear = java.time.LocalDate.now().getYear();
            String salaryQuery = "SELECT SUM(total_salary) FROM salary_calculations WHERE month = ? AND year = ?";
            PreparedStatement salaryStmt = con.prepareStatement(salaryQuery);
            salaryStmt.setInt(1, currentMonth);
            salaryStmt.setInt(2, currentYear);
            ResultSet salaryRs = salaryStmt.executeQuery();
            if (salaryRs.next()) {
                double totalSalary = salaryRs.getDouble(1);
                ((JLabel)totalSalaryCard.getComponent(1)).setText("₹" + String.format("%,.2f", totalSalary));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error updating stats: " + ex.getMessage());
        }
    }
    
    private JPanel createJobApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model
        String[] columnNames = {"ID", "Name", "Email", "Experience", "Sector", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadJobApplications(model);
            }
        });

        JButton viewDetailsBtn = createStyledButton("View Details");
        viewDetailsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int applicationId = (int) model.getValueAt(selectedRow, 0);
                    viewApplicationDetails(applicationId);
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, 
                        "Please select an application to view details.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        JButton approveBtn = createStyledButton("Approve");
        approveBtn.setBackground(new Color(60, 179, 113)); // Green color for approve
        approveBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                approveBtn.setBackground(new Color(50, 150, 90));
            }
            public void mouseExited(MouseEvent e) {
                approveBtn.setBackground(new Color(60, 179, 113));
            }
        });
        approveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int applicationId = (int) model.getValueAt(selectedRow, 0);
                    String currentStatus = (String) model.getValueAt(selectedRow, 5);
                    
                    if (currentStatus.equals("pending")) {
                        approveApplication(applicationId, model, selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this, 
                            "Only pending applications can be approved.", 
                            "Invalid Action", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, 
                        "Please select an application to approve.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(approveBtn);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load data
        loadJobApplications(model);
        
        return panel;
    }
    
    private void loadJobApplications(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT application_id, full_name, email, work_experience, interested_sector, status " +
                          "FROM job_applications ORDER BY application_id DESC";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("application_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getInt("work_experience"));
                row.add(rs.getString("interested_sector"));
                row.add(rs.getString("status"));
                row.add("Actions");
                model.addRow(row);
            }
            
            statusBar.setText("Job applications loaded successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading job applications: " + ex.getMessage());
        }
    }
    
    private void approveApplication(int applicationId, DefaultTableModel model, int selectedRow) {
        try {
            Connection con = DBConnection.getConnection();
            
            // Update application status to approved
            String updateQuery = "UPDATE job_applications SET status = 'approved' WHERE application_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setInt(1, applicationId);
            updateStmt.executeUpdate();
            
            // Create employee record
            String insertQuery = "INSERT INTO employees (employee_id, full_name, email) " +
                               "SELECT CONCAT('EMP', LPAD(?, 3, '0')), full_name, email " +
                               "FROM job_applications WHERE application_id = ?";
            
            // Get the next employee ID
            String countQuery = "SELECT COUNT(*) FROM employees";
            PreparedStatement countStmt = con.prepareStatement(countQuery);
            ResultSet countRs = countStmt.executeQuery();
            int nextId = 1;
            if (countRs.next()) {
                nextId = countRs.getInt(1) + 1;
            }
            
            PreparedStatement insertStmt = con.prepareStatement(insertQuery);
            insertStmt.setInt(1, nextId);
            insertStmt.setInt(2, applicationId);
            insertStmt.executeUpdate();
            
            // Update the table model
            model.setValueAt("approved", selectedRow, 5);
            
            statusBar.setText("Application approved successfully. Employee record created.");
            
            JOptionPane.showMessageDialog(this, 
                "Application approved successfully. Employee record created.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error approving application: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error approving application: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewApplicationDetails(int applicationId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM job_applications WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, applicationId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Create a custom dialog for application details
                JDialog detailsDialog = new JDialog(this, "Application Details", true);
                detailsDialog.setSize(600, 500);
                detailsDialog.setLocationRelativeTo(this);
                
                JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                mainPanel.setBackground(new Color(240, 240, 245));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                // Header with name and status
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(new Color(240, 240, 245));
                
                JLabel nameLabel = new JLabel(rs.getString("full_name"));
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                nameLabel.setForeground(new Color(50, 50, 50));
                headerPanel.add(nameLabel, BorderLayout.WEST);
                
                String status = rs.getString("status");
                JLabel statusLabel = new JLabel(status.toUpperCase());
                statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                if (status.equals("approved")) {
                    statusLabel.setForeground(new Color(60, 179, 113)); // Green
                } else if (status.equals("rejected")) {
                    statusLabel.setForeground(new Color(180, 70, 70)); // Red
                } else {
                    statusLabel.setForeground(new Color(255, 140, 0)); // Orange
                }
                headerPanel.add(statusLabel, BorderLayout.EAST);
                
                mainPanel.add(headerPanel, BorderLayout.NORTH);
                
                // Content panel with details and files
                JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
                contentPanel.setBackground(Color.WHITE);
                contentPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                
                // Details panel
                JPanel detailsPanel = new JPanel(new GridBagLayout());
                detailsPanel.setBackground(Color.WHITE);
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(5, 5, 5, 10);
                
                // Add details
                addDetailRow(detailsPanel, "Email:", rs.getString("email"), gbc);
                addDetailRow(detailsPanel, "Birthdate:", rs.getDate("birthdate").toString(), gbc);
                addDetailRow(detailsPanel, "Experience:", rs.getInt("work_experience") + " years", gbc);
                addDetailRow(detailsPanel, "Sector:", rs.getString("interested_sector"), gbc);
                addDetailRow(detailsPanel, "Gender:", rs.getString("gender"), gbc);
                
                contentPanel.add(detailsPanel, BorderLayout.CENTER);
                
                // Files panel
                JPanel filesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                filesPanel.setBackground(Color.WHITE);
                
                String profilePic = rs.getString("profile_pic");
                String resume = rs.getString("resume");
                
                if (profilePic != null && !profilePic.isEmpty()) {
                    JButton viewPicBtn = createStyledButton("View Profile Picture");
                    viewPicBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            viewFile(profilePic, "Profile Picture");
                        }
                    });
                    filesPanel.add(viewPicBtn);
                }
                
                if (resume != null && !resume.isEmpty()) {
                    JButton viewResumeBtn = createStyledButton("View Resume");
                    viewResumeBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            viewFile(resume, "Resume");
                        }
                    });
                    filesPanel.add(viewResumeBtn);
                }
                
                if (filesPanel.getComponentCount() > 0) {
                    contentPanel.add(filesPanel, BorderLayout.SOUTH);
                }
                
                mainPanel.add(contentPanel, BorderLayout.CENTER);
                
                // Button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
                buttonPanel.setBackground(new Color(240, 240, 245));
                
                JButton closeBtn = createStyledButton("Close");
                closeBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        detailsDialog.dispose();
                    }
                });
                
                buttonPanel.add(closeBtn);
                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                detailsDialog.add(mainPanel);
                detailsDialog.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Application not found.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error viewing application details: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error viewing application details: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addDetailRow(JPanel panel, String label, String value, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(val, gbc);
    }
    
    private void viewFile(String filePath, String title) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Desktop operations are not supported on this system.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "File not found: " + filePath, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error opening file: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openWorkAssignmentDialog(int applicationId) {
        WorkAssignmentDialog dialog = new WorkAssignmentDialog(this, applicationId);
        dialog.setVisible(true);
        
        // Refresh the job applications table after dialog closes
        JPanel jobApplicationsPanel = (JPanel) tabbedPane.getComponentAt(1);
        JScrollPane scrollPane = (JScrollPane) jobApplicationsPanel.getComponent(1);
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        loadJobApplications(model);
    }
    
    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model
        String[] columnNames = {"ID", "Name", "Department", "Designation", "UPI ID", "Mobile", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEmployees(model);
            }
        });
        
        JButton updateUpiBtn = createStyledButton("Update UPI & Mobile");
        updateUpiBtn.setBackground(new Color(60, 179, 113));
        updateUpiBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                updateUpiBtn.setBackground(new Color(50, 150, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                updateUpiBtn.setBackground(new Color(60, 179, 113));
            }
        });
        updateUpiBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String employeeId = (String) model.getValueAt(selectedRow, 0);
                    updateEmployeeUpiAndMobile(employeeId);
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, 
                        "Please select an employee to update UPI ID and mobile number.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        JButton assignWorkBtn = createStyledButton("Assign Work");
        assignWorkBtn.setBackground(new Color(60, 179, 113));
        assignWorkBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                assignWorkBtn.setBackground(new Color(50, 150, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                assignWorkBtn.setBackground(new Color(60, 179, 113));
            }
        });
        assignWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String employeeId = (String) model.getValueAt(selectedRow, 0);
                    openWorkAssignmentDialogForEmployee(employeeId);
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, 
                        "Please select an employee to assign work.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(updateUpiBtn);
        buttonPanel.add(assignWorkBtn);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load data
        loadEmployees(model);
        
        return panel;
    }
    
    private void loadEmployees(DefaultTableModel model) {
        try {
            Connection con = DBConnection.getConnection();
            
            // Clear existing data
            model.setRowCount(0);
            
            // Get all employees from both tables
            String query = "SELECT e.employee_id, e.full_name, e.email, e.department, e.designation, " +
                          "e.gender, e.upi, e.mobile_number, " +
                          "CASE WHEN u.user_id IS NOT NULL THEN 'Yes' ELSE 'No' END as has_account " +
                          "FROM employees e " +
                          "LEFT JOIN user_accounts u ON e.employee_id = u.user_id " +
                          "ORDER BY e.full_name";
            
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getString("designation"),
                    rs.getString("gender"),
                    rs.getString("upi"),
                    rs.getString("mobile_number"),
                    rs.getString("has_account")
                });
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading employees: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openWorkAssignmentDialogForEmployee(String employeeId) {
        WorkAssignmentDialog dialog = new WorkAssignmentDialog(this, employeeId);
        dialog.setVisible(true);
        // Refresh the employees table after dialog closes
        JPanel employeesPanel = (JPanel) tabbedPane.getComponentAt(2);
        JScrollPane scrollPane = (JScrollPane) employeesPanel.getComponent(1);
        JTable table = (JTable) scrollPane.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        loadEmployees(model);
    }
    
    private void updateEmployeeUpiAndMobile(String employeeId) {
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
            JDialog dialog = new JDialog(this, "Update UPI ID and Mobile Number", true);
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
                    try {
                        String updateQuery = "UPDATE employees SET upi = ?, mobile_number = ? WHERE employee_id = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                        updateStmt.setString(1, upiField.getText().trim());
                        updateStmt.setString(2, mobileField.getText().trim());
                        updateStmt.setString(3, employeeId);
                        updateStmt.executeUpdate();
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "UPI ID and mobile number updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        dialog.dispose();
                        
                        // Refresh the employees table
                        JPanel employeesPanel = (JPanel) tabbedPane.getComponentAt(2);
                        JScrollPane scrollPane = (JScrollPane) employeesPanel.getComponent(1);
                        JTable table = (JTable) scrollPane.getViewport().getView();
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        loadEmployees(model);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog, 
                            "Error updating UPI ID and mobile number: " + ex.getMessage(), 
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            JButton cancelBtn = createStyledButton("Cancel");
            cancelBtn.setBackground(new Color(220, 53, 69));
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            buttonPanel.setBackground(new Color(240, 240, 245));
            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);
            
            panel.add(buttonPanel, gbc);
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error creating update dialog: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create split pane for employee list and calendar
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setBackground(new Color(240, 240, 245));

        // Left panel - Employee list
        JPanel employeeListPanel = new JPanel(new BorderLayout());
        employeeListPanel.setBackground(new Color(240, 240, 245));
        employeeListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table model for employees
        String[] columnNames = {"Employee ID", "Name", "Department"};
        DefaultTableModel employeeModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable employeeTable = new JTable(employeeModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(30);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(new Color(70, 130, 180));
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add refresh button
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadEmployeesForAttendance(employeeModel);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.add(refreshBtn);

        employeeListPanel.add(buttonPanel, BorderLayout.NORTH);
        employeeListPanel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Right panel - Calendar
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(new Color(240, 240, 245));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create calendar
        JPanel monthPanel = new JPanel(new BorderLayout());
        monthPanel.setBackground(new Color(240, 240, 245));

        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthPanel.add(monthLabel, BorderLayout.CENTER);

        JButton prevMonthBtn = new JButton("<");
        prevMonthBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prevMonthBtn.setBackground(new Color(70, 130, 180));
        prevMonthBtn.setForeground(Color.WHITE);
        prevMonthBtn.setFocusPainted(false);
        prevMonthBtn.addActionListener(e -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateCalendarForEmployee(calendarGrid, monthLabel);
        });

        JButton nextMonthBtn = new JButton(">");
        nextMonthBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nextMonthBtn.setBackground(new Color(70, 130, 180));
        nextMonthBtn.setForeground(Color.WHITE);
        nextMonthBtn.setFocusPainted(false);
        nextMonthBtn.addActionListener(e -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateCalendarForEmployee(calendarGrid, monthLabel);
        });

        JPanel monthNavPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        monthNavPanel.setBackground(new Color(240, 240, 245));
        monthNavPanel.add(prevMonthBtn);
        monthNavPanel.add(nextMonthBtn);

        monthPanel.add(monthNavPanel, BorderLayout.EAST);

        // Calendar grid
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarGrid.setBackground(new Color(240, 240, 245));

        // Add day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setBackground(new Color(70, 130, 180));
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setOpaque(true);
            calendarGrid.add(dayLabel);
        }

        calendarPanel.add(monthPanel, BorderLayout.NORTH);
        calendarPanel.add(calendarGrid, BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setLeftComponent(employeeListPanel);
        splitPane.setRightComponent(calendarPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        // Load initial data
        loadEmployeesForAttendance(employeeModel);

        // Add selection listener to employee table
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedEmployeeId = (String) employeeModel.getValueAt(selectedRow, 0);
                    updateCalendarForEmployee(calendarGrid, monthLabel);
                }
            }
        });

        return panel;
    }

    private void loadEmployeesForAttendance(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT employee_id, full_name, department FROM employees ORDER BY employee_id";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("department")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading employees: " + ex.getMessage());
        }
    }

    private void updateCalendarForEmployee(JPanel calendarGrid, JLabel monthLabel) {
        try {
            Connection con = DBConnection.getConnection();
            
            // Update month label
            monthLabel.setText(Month.of(currentMonth).toString() + " " + currentYear);
            
            // Clear previous calendar
            calendarGrid.removeAll();
            
            // Add day headers
            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (String day : days) {
                JLabel headerLabel = new JLabel(day, SwingConstants.CENTER);
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                headerLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                calendarGrid.add(headerLabel);
            }
            
            // Get attendance data for the employee
            String query = "SELECT work_date, attendance_status FROM attendance WHERE employee_id = ? AND " +
                          "MONTH(work_date) = ? AND YEAR(work_date) = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, selectedEmployeeId);
            pst.setInt(2, currentMonth);
            pst.setInt(3, currentYear);
            ResultSet rs = pst.executeQuery();
            
            // Create a map of dates to attendance status
            Map<LocalDate, String> attendanceMap = new HashMap<>();
            while (rs.next()) {
                Date workDate = rs.getDate("work_date");
                String status = rs.getString("attendance_status");
                attendanceMap.put(workDate.toLocalDate(), status);
            }
            
            // Define public holidays
            Set<LocalDate> publicHolidays = new HashSet<>();
            publicHolidays.add(LocalDate.of(currentYear, 1, 1)); // New Year
            publicHolidays.add(LocalDate.of(currentYear, 1, 26)); // Republic Day
            publicHolidays.add(LocalDate.of(currentYear, 8, 15)); // Independence Day
            publicHolidays.add(LocalDate.of(currentYear, 10, 2)); // Gandhi Jayanti
            
            // Update calendar with attendance data
            LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
            int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // 0 = Sunday
            
            // Add empty cells for days before the first day of the month
            for (int i = 0; i < firstDayOfWeek; i++) {
                JLabel emptyLabel = new JLabel("");
                emptyLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                calendarGrid.add(emptyLabel);
            }
            
            int daysInMonth = firstDay.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                dayLabel.setOpaque(true);
                
                LocalDate date = LocalDate.of(currentYear, currentMonth, day);
                
                // Check if it's a Sunday
                if (date.getDayOfWeek().getValue() == 7) { // 7 = Sunday
                    updateDayLabelAppearance(dayLabel, "sunday");
                    dayLabel.setToolTipText("Sunday");
                }
                // Check if it's a public holiday
                else if (publicHolidays.contains(date)) {
                    updateDayLabelAppearance(dayLabel, "holiday");
                    dayLabel.setToolTipText("Public Holiday");
                } else {
                    String status = attendanceMap.get(date);
                    if (status != null) {
                        updateDayLabelAppearance(dayLabel, status);
                    }
                }

                // Add click listener for each day
                final LocalDate currentDate = date;
                dayLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!publicHolidays.contains(currentDate) && currentDate.getDayOfWeek().getValue() != 7) {
                            if (e.getClickCount() == 1) { // Single click for present
                                markAttendance(currentDate, true, dayLabel);
                            } else if (e.getClickCount() == 2) { // Double click for absent
                                markAttendance(currentDate, false, dayLabel);
                            } else if (e.getClickCount() == 3) { // Triple click for public holiday
                                markPublicHoliday(currentDate, dayLabel);
                            }
                        }
                    }
                });
                
                calendarGrid.add(dayLabel);
            }
            
            // Add empty cells for remaining days
            int remainingCells = 42 - (firstDayOfWeek + daysInMonth); // 42 = 6 rows * 7 days
            for (int i = 0; i < remainingCells; i++) {
                JLabel emptyLabel = new JLabel("");
                emptyLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                calendarGrid.add(emptyLabel);
            }
            
            calendarGrid.revalidate();
            calendarGrid.repaint();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error updating calendar: " + ex.getMessage());
        }
    }

    private void markAttendance(LocalDate date, boolean isPresent, JLabel dayLabel) {
        if (selectedEmployeeId == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first", "No Employee Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            
            // First check if record exists
            String checkQuery = "SELECT start_time FROM attendance WHERE employee_id = ? AND work_date = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, selectedEmployeeId);
            checkStmt.setDate(2, Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();
            
            String query;
            PreparedStatement pst;
            
            if (rs.next()) {
                // Update existing record
                query = "UPDATE attendance SET attendance_status = ?, start_time = COALESCE(start_time, NOW()), end_time = NOW() WHERE employee_id = ? AND work_date = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, isPresent ? "PRESENT" : "ABSENT");
                pst.setString(2, selectedEmployeeId);
                pst.setDate(3, Date.valueOf(date));
            } else {
                // Insert new record
                query = "INSERT INTO attendance (employee_id, work_date, shift_type, start_time, end_time, working_hours, attendance_status) " +
                       "VALUES (?, ?, 'FULLTIME', NOW(), NOW(), 0, ?)";
                pst = con.prepareStatement(query);
                pst.setString(1, selectedEmployeeId);
                pst.setDate(2, Date.valueOf(date));
                pst.setString(3, isPresent ? "PRESENT" : "ABSENT");
            }
            
            pst.executeUpdate();

            // Update UI immediately
            updateDayLabelAppearance(dayLabel, isPresent ? "present" : "absent");
            statusBar.setText("Attendance marked successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error marking attendance: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error marking attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markPublicHoliday(LocalDate date, JLabel dayLabel) {
        if (selectedEmployeeId == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first", "No Employee Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            
            // First check if record exists
            String checkQuery = "SELECT start_time FROM attendance WHERE employee_id = ? AND work_date = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, selectedEmployeeId);
            checkStmt.setDate(2, Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();
            
            String query;
            PreparedStatement pst;
            
            if (rs.next()) {
                // Update existing record
                query = "UPDATE attendance SET attendance_status = 'PUBLIC_HOLIDAY', start_time = COALESCE(start_time, NOW()), end_time = NOW() WHERE employee_id = ? AND work_date = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, selectedEmployeeId);
                pst.setDate(2, Date.valueOf(date));
            } else {
                // Insert new record
                query = "INSERT INTO attendance (employee_id, work_date, shift_type, start_time, end_time, working_hours, attendance_status) " +
                       "VALUES (?, ?, 'FULLTIME', NOW(), NOW(), 0, 'PUBLIC_HOLIDAY')";
                pst = con.prepareStatement(query);
                pst.setString(1, selectedEmployeeId);
                pst.setDate(2, Date.valueOf(date));
            }
            
            pst.executeUpdate();

            // Update UI immediately
            updateDayLabelAppearance(dayLabel, "holiday");
            statusBar.setText("Marked as public holiday");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error marking holiday: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error marking holiday: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDayLabelAppearance(JLabel dayLabel, String status) {
        if (status.equalsIgnoreCase("PRESENT")) {
            dayLabel.setBackground(new Color(60, 179, 113)); // Green for present
            dayLabel.setForeground(Color.WHITE);
        } else if (status.equalsIgnoreCase("ABSENT")) {
            dayLabel.setBackground(new Color(220, 53, 69)); // Red for absent
            dayLabel.setForeground(Color.WHITE);
        } else if (status.equalsIgnoreCase("PUBLIC_HOLIDAY") || status.equalsIgnoreCase("holiday")) {
            dayLabel.setBackground(new Color(0, 123, 255)); // Blue for public holidays
            dayLabel.setForeground(Color.WHITE);
        } else if (status.equalsIgnoreCase("SUNDAY")) {
            dayLabel.setBackground(new Color(220, 53, 69)); // Red for Sundays
            dayLabel.setForeground(Color.WHITE);
        }
        dayLabel.setOpaque(true);
        dayLabel.repaint();
    }
    
    private JPanel createSalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model
        String[] columnNames = {"ID", "Employee", "Month", "Year", "Base Salary", "Night Allowance", "Overtime", "Hourly Pay", "Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSalaryCalculations(model);
            }
        });
        
        JButton calculateBtn = createStyledButton("Calculate Salaries");
        calculateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateSalaries();
            }
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(calculateBtn);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load data
        loadSalaryCalculations(model);
        
        return panel;
    }
    
    private void loadSalaryCalculations(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT s.calculation_id, s.employee_id, e.full_name, s.month, s.year, " +
                          "s.base_salary, s.night_shift_allowance, s.overtime_pay, s.hourly_pay, s.total_salary " +
                          "FROM salary_calculations s JOIN employees e ON s.employee_id = e.employee_id " +
                          "ORDER BY s.year DESC, s.month DESC";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("calculation_id"));
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getInt("month"));
                row.add(rs.getInt("year"));
                row.add(String.format("₹%,.2f", rs.getDouble("base_salary")));
                row.add(String.format("₹%,.2f", rs.getDouble("night_shift_allowance")));
                row.add(String.format("₹%,.2f", rs.getDouble("overtime_pay")));
                row.add(String.format("₹%,.2f", rs.getDouble("hourly_pay")));
                row.add(String.format("₹%,.2f", rs.getDouble("total_salary")));
                model.addRow(row);
            }
            if (!hasData) {
                statusBar.setText("No salary calculations found.");
            } else {
                statusBar.setText("Salary calculations loaded successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading salary calculations: " + ex.getMessage());
        }
    }
    
    private void calculateSalaries() {
        try {
            Connection con = DBConnection.getConnection();
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            int currentYear = java.time.LocalDate.now().getYear();
            
            // Get all employees
            String empQuery = "SELECT employee_id FROM employees";
            PreparedStatement empStmt = con.prepareStatement(empQuery);
            ResultSet empRs = empStmt.executeQuery();
            
            int count = 0;
            while (empRs.next()) {
                String employeeId = empRs.getString("employee_id");
                
                // Get work assignments for the current month
                String workQuery = "SELECT SUM(hours) as total_hours, AVG(hourly_rate) as avg_rate " +
                                 "FROM work_assignments " +
                                 "WHERE employee_id = ? AND MONTH(start_date) = ? AND YEAR(start_date) = ? " +
                                 "AND status = 'active'";
                PreparedStatement workStmt = con.prepareStatement(workQuery);
                workStmt.setString(1, employeeId);
                workStmt.setInt(2, currentMonth);
                workStmt.setInt(3, currentYear);
                ResultSet workRs = workStmt.executeQuery();
                
                double totalHours = 0;
                double avgRate = 0;
                if (workRs.next()) {
                    totalHours = workRs.getDouble("total_hours");
                    avgRate = workRs.getDouble("avg_rate");
                }
                
                // Get attendance records for the current month
                String attendanceQuery = "SELECT COUNT(*) as total_days, " +
                                       "SUM(CASE WHEN attendance_status = 'absent' THEN 1 ELSE 0 END) as absent_days " +
                                       "FROM attendance " +
                                       "WHERE employee_id = ? AND MONTH(work_date) = ? AND YEAR(work_date) = ?";
                PreparedStatement attendanceStmt = con.prepareStatement(attendanceQuery);
                attendanceStmt.setString(1, employeeId);
                attendanceStmt.setInt(2, currentMonth);
                attendanceStmt.setInt(3, currentYear);
                ResultSet attendanceRs = attendanceStmt.executeQuery();
                
                int totalDays = 0;
                int absentDays = 0;
                if (attendanceRs.next()) {
                    totalDays = attendanceRs.getInt("total_days");
                    absentDays = attendanceRs.getInt("absent_days");
                }
                
                // Calculate base salary
                double baseSalary = totalHours * avgRate;
                
                // Calculate deductions for absent days (₹500 per day)
                double absentDeduction = absentDays * 500; // Fixed ₹500 per day deduction
                
                // Calculate night shift allowance (if applicable)
                double nightShiftAllowance = 0;
                String nightShiftQuery = "SELECT SUM(hours) as night_hours " +
                                       "FROM work_assignments " +
                                       "WHERE employee_id = ? AND MONTH(start_date) = ? AND YEAR(start_date) = ? " +
                                       "AND status = 'active'";
                PreparedStatement nightShiftStmt = con.prepareStatement(nightShiftQuery);
                nightShiftStmt.setString(1, employeeId);
                nightShiftStmt.setInt(2, currentMonth);
                nightShiftStmt.setInt(3, currentYear);
                ResultSet nightShiftRs = nightShiftStmt.executeQuery();
                
                if (nightShiftRs.next()) {
                    double nightHours = nightShiftRs.getDouble("night_hours");
                    nightShiftAllowance = nightHours * avgRate * 0.2; // 20% extra for night shifts
                }
                
                // Calculate overtime pay (₹300 per hour)
                double overtimePay = 0;
                String overtimeQuery = "SELECT SUM(hours) as overtime_hours " +
                                     "FROM work_assignments " +
                                     "WHERE employee_id = ? AND MONTH(start_date) = ? AND YEAR(start_date) = ? " +
                                     "AND hours > 8 AND status = 'active'";
                PreparedStatement overtimeStmt = con.prepareStatement(overtimeQuery);
                overtimeStmt.setString(1, employeeId);
                overtimeStmt.setInt(2, currentMonth);
                overtimeStmt.setInt(3, currentYear);
                ResultSet overtimeRs = overtimeStmt.executeQuery();
                
                if (overtimeRs.next()) {
                    double overtimeHours = overtimeRs.getDouble("overtime_hours");
                    overtimePay = overtimeHours * 300; // Fixed ₹300 per hour for overtime
                }
                
                // Calculate total salary
                double totalSalary = baseSalary - absentDeduction + nightShiftAllowance + overtimePay;
                
                // Insert or update salary calculation
                String checkQuery = "SELECT calculation_id FROM salary_calculations " +
                                  "WHERE employee_id = ? AND month = ? AND year = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, employeeId);
                checkStmt.setInt(2, currentMonth);
                checkStmt.setInt(3, currentYear);
                ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next()) {
                    // Update existing calculation
                    int calcId = checkRs.getInt("calculation_id");
                    String updateQuery = "UPDATE salary_calculations SET " +
                                       "base_salary = ?, absent_deduction = ?, night_shift_allowance = ?, " +
                                       "overtime_pay = ?, hourly_pay = ?, total_salary = ? " +
                                       "WHERE calculation_id = ?";
                    PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, baseSalary);
                    updateStmt.setDouble(2, absentDeduction);
                    updateStmt.setDouble(3, nightShiftAllowance);
                    updateStmt.setDouble(4, overtimePay);
                    updateStmt.setDouble(5, avgRate);
                    updateStmt.setDouble(6, totalSalary);
                    updateStmt.setInt(7, calcId);
                    updateStmt.executeUpdate();
                } else {
                    // Insert new calculation
                    String insertQuery = "INSERT INTO salary_calculations " +
                                       "(employee_id, month, year, base_salary, absent_deduction, " +
                                       "night_shift_allowance, overtime_pay, hourly_pay, total_salary) " +
                                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                    insertStmt.setString(1, employeeId);
                    insertStmt.setInt(2, currentMonth);
                    insertStmt.setInt(3, currentYear);
                    insertStmt.setDouble(4, baseSalary);
                    insertStmt.setDouble(5, absentDeduction);
                    insertStmt.setDouble(6, nightShiftAllowance);
                    insertStmt.setDouble(7, overtimePay);
                    insertStmt.setDouble(8, avgRate);
                    insertStmt.setDouble(9, totalSalary);
                    insertStmt.executeUpdate();
                }
                
                count++;
            }
            
            statusBar.setText("Salaries calculated for " + count + " employees.");
            JOptionPane.showMessageDialog(this, 
                "Salaries calculated for " + count + " employees.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh salary table
            JPanel salaryPanel = (JPanel) tabbedPane.getComponentAt(4);
            JScrollPane scrollPane = (JScrollPane) salaryPanel.getComponent(1);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            loadSalaryCalculations(model);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error calculating salaries: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error calculating salaries: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // A helper method to create a styled JButton
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

    // Add the Pay Salary panel
    private JPanel createPaySalaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"Employee ID", "Name", "Month", "Year", "Total Salary", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));

        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadPendingSalaries(model);
            }
        });

        JButton payBtn = createStyledButton("Pay");
        payBtn.setBackground(new Color(60, 179, 113));
        payBtn.setEnabled(false);
        payBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (payBtn.isEnabled()) payBtn.setBackground(new Color(50, 150, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                payBtn.setBackground(new Color(60, 179, 113));
            }
        });
        payBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String employeeId = (String) model.getValueAt(selectedRow, 0);
                    String name = (String) model.getValueAt(selectedRow, 1);
                    int month = (int) model.getValueAt(selectedRow, 2);
                    int year = (int) model.getValueAt(selectedRow, 3);
                    String totalSalaryStr = (String) model.getValueAt(selectedRow, 4);
                    double totalSalary = Double.parseDouble(totalSalaryStr.replaceAll("[^0-9.]", ""));
                    if (totalSalary > 0) {
                        try (Connection con = DBConnection.getConnection()) {
                            // Get employee's UPI and mobile details
                            String upiQuery = "SELECT e.upi, e.mobile_number, e.full_name, e.employee_id, e.department " +
                                            "FROM employees e WHERE e.employee_id = ?";
                            PreparedStatement upiStmt = con.prepareStatement(upiQuery);
                            upiStmt.setString(1, employeeId);
                            ResultSet upiRs = upiStmt.executeQuery();
                            
                            if (upiRs.next()) {
                                String upi = upiRs.getString("upi");
                                String mobileNumber = upiRs.getString("mobile_number");
                                String fullName = upiRs.getString("full_name");
                                String empId = upiRs.getString("employee_id");
                                String department = upiRs.getString("department");
                                
                                if (upi == null || upi.isEmpty() || mobileNumber == null || mobileNumber.isEmpty()) {
                                    JOptionPane.showMessageDialog(panel, 
                                        "Employee's UPI ID and mobile number are not set. Please ask the employee to update their details first.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                
                                // Create UPI string for salary payment
                                String upiString = String.format("upi://pay?pa=%s&pn=%s&am=%.2f&tn=Salary%%20Payment%%20-%s%%20(%s)&cu=INR", 
                                    upi, 
                                    fullName,
                                    totalSalary,
                                    empId,
                                    department);
                                
                                // Generate QR code
                                ImageIcon qrIcon = null;
                                try {
                                    BitMatrix bitMatrix = new MultiFormatWriter().encode(upiString, BarcodeFormat.QR_CODE, 300, 300);
                                    BufferedImage qrImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
                                    qrIcon = new ImageIcon(qrImg);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                
                                JLabel qrLabel = qrIcon != null ? new JLabel(qrIcon) : new JLabel("[QR Code]");
                                JPanel qrPanel = new JPanel(new BorderLayout());
                                qrPanel.add(new JLabel("Scan to pay:"), BorderLayout.NORTH);
                                qrPanel.add(qrLabel, BorderLayout.CENTER);
                                
                                // Custom dialog with 'Pay' button
                                JDialog payDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel), "Pay Salary", true);
                                payDialog.setSize(450, 550); // Further increased size
                                payDialog.setLocationRelativeTo(panel);
                                payDialog.setUndecorated(true);
                                
                                JPanel dialogPanel = new JPanel(new BorderLayout(0, 0));
                                dialogPanel.setBackground(new Color(245, 250, 255));
                                dialogPanel.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2, true),
                                    BorderFactory.createEmptyBorder(25, 25, 25, 25) // Increased padding
                                ));
                                
                                // Header
                                JLabel header = new JLabel("Salary Payment", SwingConstants.CENTER);
                                header.setFont(new Font("Segoe UI", Font.BOLD, 20));
                                header.setForeground(new Color(60, 120, 180));
                                dialogPanel.add(header, BorderLayout.NORTH);
                                
                                // QR and info
                                JPanel qrInfoPanel = new JPanel();
                                qrInfoPanel.setLayout(new BoxLayout(qrInfoPanel, BoxLayout.Y_AXIS));
                                qrInfoPanel.setOpaque(false);
                                
                                JLabel amountLabel = new JLabel(String.format("Amount: ₹%,.2f", totalSalary));
                                amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                                amountLabel.setForeground(new Color(40, 100, 60));
                                amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                                
                                JLabel empLabel = new JLabel(fullName + " (" + empId + ")");
                                empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                                empLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                                
                                JLabel upiLabel = new JLabel("UPI: " + upi);
                                upiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                upiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                                
                                qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                                qrLabel.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                                    BorderFactory.createEmptyBorder(15, 15, 15, 15) // Increased padding around QR
                                ));
                                
                                qrInfoPanel.add(Box.createVerticalStrut(15)); // Increased spacing
                                qrInfoPanel.add(amountLabel);
                                qrInfoPanel.add(Box.createVerticalStrut(10)); // Increased spacing
                                qrInfoPanel.add(empLabel);
                                qrInfoPanel.add(Box.createVerticalStrut(10)); // Increased spacing
                                qrInfoPanel.add(upiLabel);
                                qrInfoPanel.add(Box.createVerticalStrut(20)); // Increased spacing
                                qrInfoPanel.add(qrLabel);
                                qrInfoPanel.add(Box.createVerticalStrut(15)); // Increased spacing
                                
                                JSeparator sep = new JSeparator();
                                sep.setMaximumSize(new Dimension(350, 1)); // Increased width
                                qrInfoPanel.add(sep);
                                dialogPanel.add(qrInfoPanel, BorderLayout.CENTER);
                                
                                // Pay button
                                JButton payButton = new JButton("Pay");
                                payButton.setBackground(new Color(60, 179, 113));
                                payButton.setForeground(Color.WHITE);
                                payButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                                payButton.setFocusPainted(false);
                                payButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
                                payButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                
                                payButton.addMouseListener(new java.awt.event.MouseAdapter() {
                                    public void mouseEntered(java.awt.event.MouseEvent e) {
                                        payButton.setBackground(new Color(50, 150, 90));
                                    }
                                    public void mouseExited(java.awt.event.MouseEvent e) {
                                        payButton.setBackground(new Color(60, 179, 113));
                                    }
                                });
                                
                                payButton.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        // Prompt for PIN
                                        String pin = JOptionPane.showInputDialog(payDialog, "Enter PIN to confirm payment:", "PIN Required", JOptionPane.PLAIN_MESSAGE);
                                        if (pin != null && pin.equals("1234")) {
                                            // Mark as paid (set total_salary to 0)
                                            try (Connection con = DBConnection.getConnection()) {
                                                String updateQuery = "UPDATE salary_calculations SET total_salary = 0 WHERE employee_id = ? AND month = ? AND year = ?";
                                                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                                                updateStmt.setString(1, employeeId);
                                                updateStmt.setInt(2, month);
                                                updateStmt.setInt(3, year);
                                                updateStmt.executeUpdate();
                                                
                                                // Insert into salary_payments
                                                String insertPayment = "INSERT INTO salary_payments (employee_id, payment_date, amount) VALUES (?, CURDATE(), ?)";
                                                PreparedStatement payStmt = con.prepareStatement(insertPayment);
                                                payStmt.setString(1, employeeId);
                                                payStmt.setDouble(2, totalSalary);
                                                payStmt.executeUpdate();
                                                
                                                JOptionPane.showMessageDialog(panel, "Payment complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                                payDialog.dispose();
                                                loadPendingSalaries(model);
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                JOptionPane.showMessageDialog(panel, "Error updating payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else if (pin != null) {
                                            JOptionPane.showMessageDialog(payDialog, "Incorrect PIN. Payment not processed.", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                });
                                
                                JPanel btnPanel = new JPanel();
                                btnPanel.setOpaque(false);
                                btnPanel.add(payButton);
                                dialogPanel.add(btnPanel, BorderLayout.SOUTH);
                                payDialog.setContentPane(dialogPanel);
                                payDialog.setVisible(true);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(panel, "Error fetching employee details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String totalSalaryStr = (String) model.getValueAt(selectedRow, 4);
                double totalSalary = Double.parseDouble(totalSalaryStr.replaceAll("[^0-9.]", ""));
                payBtn.setEnabled(totalSalary > 0);
            } else {
                payBtn.setEnabled(false);
            }
        });

        buttonPanel.add(refreshBtn);
        buttonPanel.add(payBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadPendingSalaries(model);

        // Add custom table cell renderer for card style
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                if (isSelected) {
                    c.setBackground(new Color(220, 240, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }
                setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                return c;
            }
        });

        return panel;
    }

    private void loadPendingSalaries(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            int currentMonth = java.time.LocalDate.now().getMonthValue();
            int currentYear = java.time.LocalDate.now().getYear();
            String query = "SELECT s.employee_id, e.full_name, s.month, s.year, s.total_salary FROM salary_calculations s JOIN employees e ON s.employee_id = e.employee_id WHERE s.total_salary > 0 AND s.month = ? AND s.year = ? ORDER BY s.total_salary DESC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, currentMonth);
            pst.setInt(2, currentYear);
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getInt("month"));
                row.add(rs.getInt("year"));
                row.add(String.format("₹%,.2f", rs.getDouble("total_salary")));
                row.add("Pending");
                model.addRow(row);
            }
            if (!hasData) {
                statusBar.setText("No pending salaries found.");
            } else {
                statusBar.setText("Pending salaries loaded successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading pending salaries: " + ex.getMessage());
        }
    }

    // Add Salary History panel
    private JPanel createSalaryHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columnNames = {"Employee ID", "Name", "Payment Date", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        JButton refreshBtn = createStyledButton("Refresh");
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSalaryHistory(model);
            }
        });
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        loadSalaryHistory(model);
        return panel;
    }
    private void loadSalaryHistory(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT p.employee_id, e.full_name, p.payment_date, p.amount FROM salary_payments p JOIN employees e ON p.employee_id = e.employee_id ORDER BY p.payment_date DESC";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getDate("payment_date") != null ? rs.getDate("payment_date").toString() : "");
                row.add(String.format("₹%,.2f", rs.getDouble("amount")));
                model.addRow(row);
            }
            if (!hasData) {
                statusBar.setText("No salary payments found.");
            } else {
                statusBar.setText("Salary payment history loaded successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading salary history: " + ex.getMessage());
        }
    }

    private String generateNewEmployeeId() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM employees");
             ResultSet rs = pstmt.executeQuery()) {
            
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return "EMP" + String.format("%04d", nextId);
        }
    }

    // Main method to launch the AdminDashboard directly.
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }
}
