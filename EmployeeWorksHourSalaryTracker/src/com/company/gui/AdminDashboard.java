package com.company.gui;

import com.company.database.DBConnection;
import com.company.utils.QRCodeGenerator;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.image.BufferedImage;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

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
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
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
        
        JButton assignWorkBtn = createStyledButton("Assign Work");
        assignWorkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int applicationId = (int) model.getValueAt(selectedRow, 0);
                    String status = (String) model.getValueAt(selectedRow, 5);
                    if (status.equals("approved")) {
                        openWorkAssignmentDialog(applicationId);
                    } else {
                        JOptionPane.showMessageDialog(AdminDashboard.this, 
                            "Only approved applications can be assigned work.", 
                            "Invalid Action", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, 
                        "Please select an application to assign work.", 
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(assignWorkBtn);
        
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
    
    private void viewApplicationDetails(int applicationId) {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM job_applications WHERE application_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, applicationId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("<html><body style='font-family: Segoe UI;'>");
                details.append("<h2>Application Details</h2>");
                details.append("<p><b>ID:</b> ").append(rs.getInt("application_id")).append("</p>");
                details.append("<p><b>Name:</b> ").append(rs.getString("full_name")).append("</p>");
                details.append("<p><b>Email:</b> ").append(rs.getString("email")).append("</p>");
                details.append("<p><b>Birthdate:</b> ").append(rs.getDate("birthdate")).append("</p>");
                details.append("<p><b>Experience:</b> ").append(rs.getInt("work_experience")).append(" years</p>");
                details.append("<p><b>Gender:</b> ").append(rs.getString("gender")).append("</p>");
                details.append("<p><b>Sector:</b> ").append(rs.getString("interested_sector")).append("</p>");
                details.append("<p><b>Status:</b> ").append(rs.getString("status")).append("</p>");
                details.append("<p><b>Profile Picture:</b> ").append(rs.getString("profile_pic")).append("</p>");
                details.append("<p><b>Resume:</b> ").append(rs.getString("resume")).append("</p>");
                details.append("</body></html>");
                
                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setText(details.toString());
                textPane.setEditable(false);
                
                JScrollPane scrollPane = new JScrollPane(textPane);
                scrollPane.setPreferredSize(new Dimension(500, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Application Details", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error viewing application details: " + ex.getMessage());
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
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addEmployeeBtn);
        
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
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("attendance_id"));
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getDate("work_date"));
                row.add(rs.getString("shift_type"));
                row.add(rs.getTime("start_time"));
                row.add(rs.getTime("end_time") != null ? rs.getTime("end_time") : "Not clocked out");
                row.add(rs.getDouble("working_hours") != 0 ? rs.getDouble("working_hours") : "In progress");
                model.addRow(row);
            }
            
            statusBar.setText("Attendance records loaded successfully.");
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
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("calculation_id"));
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getInt("month"));
                row.add(rs.getInt("year"));
                row.add(rs.getDouble("base_salary"));
                row.add(rs.getDouble("night_shift_allowance"));
                row.add(rs.getDouble("overtime_pay"));
                row.add(rs.getDouble("hourly_pay"));
                row.add(rs.getDouble("total_salary"));
                model.addRow(row);
            }
            
            statusBar.setText("Salary calculations loaded successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusBar.setText("Error loading salary calculations: " + ex.getMessage());
        }
    }
    
    private void calculateSalaries() {
        // TODO: Implement salary calculation logic
        JOptionPane.showMessageDialog(this, 
            "Salary calculation functionality will be implemented soon.", 
            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
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
