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

public class AdminDashboard extends JFrame {
  
    // A status label at the bottom serves as our status bar.
    private JLabel statusBar;
    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
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
        String[] columnNames = {"ID", "Name", "Email", "Department", "Designation", "Actions"};
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
        
        JButton addEmployeeBtn = createStyledButton("Add Employee");
        addEmployeeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement add employee functionality
                JOptionPane.showMessageDialog(AdminDashboard.this, 
                    "Add Employee functionality will be implemented soon.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
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
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(assignWorkBtn);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load data
        loadEmployees(model);
        
        return panel;
    }
    
    private void loadEmployees(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT employee_id, full_name, email, department, designation FROM employees";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("department") != null ? rs.getString("department") : "");
                row.add(rs.getString("designation") != null ? rs.getString("designation") : "");
                row.add("Actions");
                model.addRow(row);
            }
            
            statusBar.setText("Employees loaded successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading employees: " + ex.getMessage());
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
    
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model
        String[] columnNames = {"ID", "Employee", "Date", "Shift", "Start Time", "End Time", "Hours"};
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
                loadAttendance(model);
            }
        });
        
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load data
        loadAttendance(model);
        
        return panel;
    }
    
    private void loadAttendance(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT a.attendance_id, a.employee_id, e.full_name, a.work_date, " +
                          "a.shift_type, a.start_time, a.end_time, a.working_hours " +
                          "FROM attendance a JOIN employees e ON a.employee_id = e.employee_id " +
                          "ORDER BY a.work_date DESC, a.start_time DESC";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("attendance_id"));
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getDate("work_date") != null ? rs.getDate("work_date").toString() : "");
                row.add(rs.getString("shift_type"));
                row.add(rs.getTime("start_time") != null ? rs.getTime("start_time").toString() : "");
                row.add(rs.getTime("end_time") != null ? rs.getTime("end_time").toString() : "Not clocked out");
                row.add(rs.getDouble("working_hours") != 0 ? rs.getDouble("working_hours") : "In progress");
                model.addRow(row);
            }
            if (!hasData) {
                statusBar.setText("No attendance records found.");
            } else {
                statusBar.setText("Attendance records loaded successfully.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading attendance records: " + ex.getMessage());
        }
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
                // Sum up work assignments for this employee for the current month
                String workQuery = "SELECT SUM(hours) as total_hours, AVG(hourly_rate) as avg_rate FROM work_assignments WHERE employee_id = ? AND MONTH(start_date) = ? AND YEAR(start_date) = ? AND status = 'active'";
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
                // Calculate salary (basic: hours * rate)
                double baseSalary = totalHours * avgRate;
                double nightShiftAllowance = 0; // You can add logic for this
                double overtimePay = 0; // You can add logic for this
                double hourlyPay = baseSalary; // For now, same as base
                double totalSalary = baseSalary + nightShiftAllowance + overtimePay;
                // Insert or update salary_calculations
                String checkQuery = "SELECT calculation_id FROM salary_calculations WHERE employee_id = ? AND month = ? AND year = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, employeeId);
                checkStmt.setInt(2, currentMonth);
                checkStmt.setInt(3, currentYear);
                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next()) {
                    // Update
                    int calcId = checkRs.getInt("calculation_id");
                    String updateQuery = "UPDATE salary_calculations SET base_salary = ?, night_shift_allowance = ?, overtime_pay = ?, hourly_pay = ?, total_salary = ? WHERE calculation_id = ?";
                    PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, baseSalary);
                    updateStmt.setDouble(2, nightShiftAllowance);
                    updateStmt.setDouble(3, overtimePay);
                    updateStmt.setDouble(4, hourlyPay);
                    updateStmt.setDouble(5, totalSalary);
                    updateStmt.setInt(6, calcId);
                    updateStmt.executeUpdate();
                } else {
                    // Insert
                    String insertQuery = "INSERT INTO salary_calculations (employee_id, month, year, base_salary, night_shift_allowance, overtime_pay, hourly_pay, total_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                    insertStmt.setString(1, employeeId);
                    insertStmt.setInt(2, currentMonth);
                    insertStmt.setInt(3, currentYear);
                    insertStmt.setDouble(4, baseSalary);
                    insertStmt.setDouble(5, nightShiftAllowance);
                    insertStmt.setDouble(6, overtimePay);
                    insertStmt.setDouble(7, hourlyPay);
                    insertStmt.setDouble(8, totalSalary);
                    insertStmt.executeUpdate();
                }
                count++;
            }
            statusBar.setText("Salaries calculated for " + count + " employees.");
            JOptionPane.showMessageDialog(this, "Salaries calculated for " + count + " employees.", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh salary table
            JPanel salaryPanel = (JPanel) tabbedPane.getComponentAt(4);
            JScrollPane scrollPane = (JScrollPane) salaryPanel.getComponent(1);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            loadSalaryCalculations(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error calculating salaries: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error calculating salaries: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                        // Generate QR code
                        String qrContent = "Pay ₹" + totalSalary + " to " + name + " (" + employeeId + ")";
                        ImageIcon qrIcon = null;
                        try {
                            java.awt.image.BufferedImage qrImg = QRCodeGenerator.generateQRCode(qrContent, 200, Color.WHITE, Color.BLACK, ErrorCorrectionLevel.H);
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
                        payDialog.setSize(350, 420);
                        payDialog.setLocationRelativeTo(panel);
                        payDialog.setUndecorated(true);
                        JPanel dialogPanel = new JPanel(new BorderLayout(0, 0));
                        dialogPanel.setBackground(new Color(245, 250, 255));
                        dialogPanel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(70, 130, 180), 2, true),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)
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
                        JLabel empLabel = new JLabel(name + " (" + employeeId + ")");
                        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                        empLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        qrLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                        ));
                        qrInfoPanel.add(Box.createVerticalStrut(10));
                        qrInfoPanel.add(amountLabel);
                        qrInfoPanel.add(Box.createVerticalStrut(5));
                        qrInfoPanel.add(empLabel);
                        qrInfoPanel.add(Box.createVerticalStrut(15));
                        qrInfoPanel.add(qrLabel);
                        qrInfoPanel.add(Box.createVerticalStrut(10));
                        JSeparator sep = new JSeparator();
                        sep.setMaximumSize(new Dimension(300, 1));
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
                                    try {
                                        Connection con = DBConnection.getConnection();
                                        String updateQuery = "UPDATE salary_calculations SET total_salary = 0 WHERE employee_id = ? AND month = ? AND year = ?";
                                        PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                                        updateStmt.setString(1, employeeId);
                                        updateStmt.setInt(2, month);
                                        updateStmt.setInt(3, year);
                                        updateStmt.executeUpdate();
                                        // Optionally, insert into salary_payments
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
