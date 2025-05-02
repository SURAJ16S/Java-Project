package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.table.*;
import java.util.regex.Pattern;

public class DeveloperDashboard extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(DeveloperDashboard.class.getName());
    private final String developerId;
    private JTable userTable;
    private JTable projectTable;
    private JTable recentActivitiesTable;
    private JTable attendanceTable;
    private JTable salaryTable;
    private JTable workTable;
    private JTable workRecordsTable;
    private JTextField searchField;
    private JTextField usernameField;
    private JTextField fullNameField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> statusComboBox;
    private JLabel totalEmployeesLabel;
    private JLabel activeEmployeesLabel;
    private JLabel onLeaveEmployeesLabel;
    private JLabel totalProjectsLabel;
    private JLabel inProgressProjectsLabel;
    private JLabel completedProjectsLabel;
    private JLabel totalTasksLabel;
    private JLabel pendingTasksLabel;
    private JLabel completedTasksLabel;
    private JLabel totalAttendanceLabel;
    private JLabel presentAttendanceLabel;
    private JLabel absentAttendanceLabel;
    private JPanel mainPanel;
    private Timer autoRefreshTimer;
    private String developerName;
    private JPanel userManagementPanel;
    private JTextField dateField;
    private JComboBox<String> shiftTypeCombo;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JComboBox<String> workTypeCombo;
    private JTextField taskField;
    private JTextArea descriptionArea;
    private JLabel totalHoursLabel;
    private JLabel productiveHoursLabel;
    private JLabel overtimeHoursLabel;
    private DefaultTableModel userTableModel;
    private JLabel totalUsersLabel;
    
    // Default constructor for backward compatibility
    public DeveloperDashboard() {
        this("DEV001"); // Default developer ID
    }
    
    public DeveloperDashboard(String developerId) {
        this.developerId = developerId;
        loadDeveloperInfo();
        setTitle("Developer Dashboard - " + developerName);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Set up auto-refresh timer (refresh every 30 seconds)
        autoRefreshTimer = new Timer(30000, e -> refreshDashboard());
        autoRefreshTimer.start();
        
        initComponents();
        loadUsers();
        loadWorkRecords();
    }
    
    private void loadDeveloperInfo() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT e.full_name FROM employees e " +
                         "JOIN user_accounts u ON e.employee_id = u.user_id " +
                         "WHERE u.user_id = ? AND u.role = 'developer'";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, developerId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        developerName = rs.getString("full_name");
                    } else {
                        developerName = "Developer";
                        LOGGER.warning("Developer info not found for ID: " + developerId);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading developer info", ex);
            developerName = "Developer";
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }
    
    private void initComponents() {
        // Main panel with a nice background
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with header and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 245));
        
        // Header panel with title and welcome message
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Welcome, " + developerName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
        
        // Logout panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(240, 240, 245));
        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.setBackground(new Color(180, 70, 70));
        logoutBtn.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutBtn);
        
        topPanel.add(headerPanel, BorderLayout.CENTER);
        topPanel.add(logoutPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // User Management Panel
        tabbedPane.addTab("User Management", createUserManagementPanel());
        
        // Attendance Management Panel
        tabbedPane.addTab("Attendance Management", createAttendanceManagementPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
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
    
    private JPanel createUserManagementPanel() {
        JPanel userManagementPanel = new JPanel(new BorderLayout());
        userManagementPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "User Management",
            TitledBorder.LEFT, TitledBorder.TOP));

        // Top panel with search and total users
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> searchUsers());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Total users panel
        JPanel totalUsersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalUsersLabel = new JLabel("Total Users: 0");
        totalUsersLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        totalUsersPanel.add(totalUsersLabel);
        topPanel.add(totalUsersPanel, BorderLayout.EAST);

        userManagementPanel.add(topPanel, BorderLayout.NORTH);

        // User table
        String[] columns = {"User ID", "Username", "Role", "Full Name", "Email", "Department", "Designation"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        userManagementPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = createStyledButton("Add User");
        JButton editButton = createStyledButton("Edit User");
        JButton deleteButton = createStyledButton("Delete User");
        JButton viewDetailsButton = createStyledButton("View Details");
        JButton refreshButton = createStyledButton("Refresh");

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        viewDetailsButton.addActionListener(e -> showUserDetails());
        refreshButton.addActionListener(e -> loadUsers());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(viewDetailsButton);
        actionPanel.add(refreshButton);
        userManagementPanel.add(actionPanel, BorderLayout.SOUTH);

        return userManagementPanel;
    }
    
    private JPanel createAttendanceManagementPanel() {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Attendance Management",
            TitledBorder.LEFT, TitledBorder.TOP));
            
        // Attendance form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(dateField, gbc);
        
        // Work Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Work Type:"), gbc);
        gbc.gridx = 1;
        workTypeCombo = new JComboBox<>(new String[]{"Development", "Testing", "Documentation", "Meeting", "Support"});
        formPanel.add(workTypeCombo, gbc);
        
        // Project/Task
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Project/Task:"), gbc);
        gbc.gridx = 1;
        taskField = new JTextField(20);
        formPanel.add(taskField, gbc);
        
        // Start time
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(10);
        formPanel.add(startTimeField, gbc);
        
        // End time
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);
        gbc.gridx = 1;
        endTimeField = new JTextField(10);
        formPanel.add(endTimeField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Add button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton addButton = createStyledButton("Add Work Record");
        addButton.addActionListener(e -> addWorkRecord());
        formPanel.add(addButton, gbc);
        
        attendancePanel.add(formPanel, BorderLayout.NORTH);
        
        // Work records table
        String[] columns = {"Date", "Work Type", "Project/Task", "Start Time", "End Time", "Hours", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        workRecordsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(workRecordsTable);
        attendancePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        totalHoursLabel = new JLabel("Total Hours: 0");
        totalHoursLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryPanel.add(totalHoursLabel);
        
        productiveHoursLabel = new JLabel("Productive Hours: 0");
        productiveHoursLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryPanel.add(productiveHoursLabel);
        
        overtimeHoursLabel = new JLabel("Overtime Hours: 0");
        overtimeHoursLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryPanel.add(overtimeHoursLabel);
        
        attendancePanel.add(summaryPanel, BorderLayout.SOUTH);
        
        return attendancePanel;
    }
    
    private void loadUsers() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            
            // Get total users count from all relevant tables
            String countSql = "SELECT COUNT(*) FROM user_accounts";
            try (PreparedStatement countStmt = connection.prepareStatement(countSql);
                 ResultSet countRs = countStmt.executeQuery()) {
                if (countRs.next()) {
                    totalUsersLabel.setText("Total Users: " + countRs.getInt(1));
                }
            }

            // Load all users with their details
            String sql = "SELECT u.user_id, u.username, u.role, " +
                        "COALESCE(e.full_name, j.full_name) as full_name, " +
                        "COALESCE(e.email, j.email) as email, " +
                        "e.department, e.designation " +
                        "FROM user_accounts u " +
                        "LEFT JOIN employees e ON u.user_id = e.employee_id " +
                        "LEFT JOIN job_applications j ON u.user_id = j.application_id " +
                        "ORDER BY u.user_id";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                userTableModel.setRowCount(0);
                
                while (rs.next()) {
                    userTableModel.addRow(new Object[]{
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("designation")
                    });
            }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }
    
    private void searchUsers() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
            loadUsers();
            return;
        }
        
            String sql = "SELECT u.user_id, u.username, u.role, " +
                        "COALESCE(e.full_name, j.full_name) as full_name, " +
                        "COALESCE(e.email, j.email) as email, " +
                        "e.department, e.designation " +
                        "FROM user_accounts u " +
                        "LEFT JOIN employees e ON u.user_id = e.employee_id " +
                        "LEFT JOIN job_applications j ON u.user_id = j.application_id " +
                        "WHERE u.username LIKE ? OR " +
                        "COALESCE(e.full_name, j.full_name) LIKE ? OR " +
                        "COALESCE(e.email, j.email) LIKE ? OR " +
                        "e.department LIKE ? OR e.designation LIKE ? " +
                        "ORDER BY u.user_id";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                String searchPattern = "%" + searchText + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
                stmt.setString(5, searchPattern);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    userTableModel.setRowCount(0);
                    
                    while (rs.next()) {
                        userTableModel.addRow(new Object[]{
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("designation")
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error searching users: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Stop the auto-refresh timer
            if (autoRefreshTimer != null) {
                autoRefreshTimer.stop();
            }
            
            // Log the logout activity
        try {
            Connection conn = DBConnection.getConnection();
                String query = "INSERT INTO activity_logs (user_id, activity_type, details, status) " +
                             "VALUES (?, 'LOGOUT', 'Developer logged out', 'SUCCESS')";
            PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, developerId);
                pst.executeUpdate();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to log logout activity", e);
            }
            
            // Show login frame and dispose current frame
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
    
    private void refreshDashboard() {
        loadUsers();
        loadWorkRecords();
        updateSummary();
    }

    private void addWorkRecord() {
        try {
            String date = dateField.getText();
            String workType = (String) workTypeCombo.getSelectedItem();
            String task = taskField.getText();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();
            String description = descriptionArea.getText();
            
            // Calculate working hours
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date start = timeFormat.parse(startTime);
            Date end = timeFormat.parse(endTime);
            long diff = end.getTime() - start.getTime();
            double workingHours = diff / (1000.0 * 60 * 60);
            
            // Calculate overtime (hours beyond 8)
            double overtimeHours = Math.max(0, workingHours - 8);
            double productiveHours = workingHours - overtimeHours;
            
            Connection connection = DBConnection.getConnection();
            String sql = "INSERT INTO work_records (employee_id, work_date, work_type, task, start_time, end_time, " +
                        "working_hours, overtime_hours, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, developerId);
                stmt.setString(2, date);
                stmt.setString(3, workType);
                stmt.setString(4, task);
                stmt.setString(5, startTime);
                stmt.setString(6, endTime);
                stmt.setDouble(7, workingHours);
                stmt.setDouble(8, overtimeHours);
                stmt.setString(9, description);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Work record added successfully");
                loadWorkRecords();
                updateSummary();
            }
        } catch (Exception ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error adding work record: " + ex.getMessage());
        }
    }

    private void loadWorkRecords() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT * FROM work_records WHERE employee_id = ? ORDER BY work_date DESC, start_time DESC";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, developerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) workRecordsTable.getModel();
                    model.setRowCount(0);
                    
            while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getDate("work_date"),
                            rs.getString("work_type"),
                            rs.getString("task"),
                            rs.getTime("start_time"),
                            rs.getTime("end_time"),
                            rs.getDouble("working_hours"),
                            rs.getString("description")
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading work records: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }

    private void updateSummary() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT SUM(working_hours) as total_hours, " +
                        "SUM(CASE WHEN working_hours <= 8 THEN working_hours ELSE 8 END) as productive_hours, " +
                        "SUM(overtime_hours) as overtime_hours " +
                        "FROM work_records WHERE employee_id = ? AND work_date = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, developerId);
                stmt.setString(2, dateField.getText());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        totalHoursLabel.setText("Total Hours: " + String.format("%.2f", rs.getDouble("total_hours")));
                        productiveHoursLabel.setText("Productive Hours: " + String.format("%.2f", rs.getDouble("productive_hours")));
                        overtimeHoursLabel.setText("Overtime Hours: " + String.format("%.2f", rs.getDouble("overtime_hours")));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error updating summary: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }

    private void updateStatCard(String title, String value) {
        JPanel statsPanel = (JPanel)((JPanel)mainPanel.getComponent(1)).getComponent(0);
        for (Component comp : statsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel)comp;
                JLabel titleLabel = (JLabel)((BorderLayout)card.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                if (titleLabel.getText().equals(title)) {
                    JLabel valueLabel = (JLabel)((BorderLayout)card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    valueLabel.setText(value);
                    break;
                }
            }
        }
    }

    private void loadRecentActivities() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT a.activity_type, a.description, a.created_at, u.username " +
                        "FROM activities a " +
                        "JOIN user_accounts u ON a.user_id = u.id " +
                        "ORDER BY a.created_at DESC LIMIT 10";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                DefaultTableModel model = (DefaultTableModel) recentActivitiesTable.getModel();
                model.setRowCount(0);
                
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("activity_type"),
                        rs.getString("description"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading recent activities: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }

    private void loadProjects() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String sql = "SELECT id, name, description, start_date, end_date FROM projects";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
                model.setRowCount(0);
                
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading projects: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }

    private void loadUserDetails(String userId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT u.*, e.full_name FROM user_accounts u " +
                         "JOIN employees e ON u.user_id = e.employee_id " +
                         "WHERE u.user_id = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        // Update form fields with user details
                        usernameField.setText(rs.getString("username"));
                        fullNameField.setText(rs.getString("full_name"));
                        roleComboBox.setSelectedItem(rs.getString("role"));
                        statusComboBox.setSelectedItem(rs.getString("status"));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading user details: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }

    private void loadSalaryRecords(String userId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            String query = "SELECT month, year, basic_salary, overtime_pay, deductions, net_salary " +
                         "FROM salary_records WHERE user_id = ? ORDER BY year DESC, month DESC";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    DefaultTableModel model = (DefaultTableModel) salaryTable.getModel();
                    model.setRowCount(0);
                    
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("month"),
                            rs.getInt("year"),
                            rs.getDouble("basic_salary"),
                            rs.getDouble("overtime_pay"),
                            rs.getDouble("deductions"),
                            rs.getDouble("net_salary")
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading salary records: " + ex.getMessage());
        } finally {
            if (connection != null) {
                DBConnection.closeConnection();
            }
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        JTextField fullNameField = new JTextField(20);
        panel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"employee", "developer", "admin"});
        panel.add(roleCombo, gbc);
        
        // Department
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        JTextField departmentField = new JTextField(20);
        panel.add(departmentField, gbc);

        // Designation
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Designation:"), gbc);
        gbc.gridx = 1;
        JTextField designationField = new JTextField(20);
        panel.add(designationField, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JButton saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                String fullName = fullNameField.getText().trim();
                String email = emailField.getText().trim();
                String role = (String) roleCombo.getSelectedItem();
                String department = departmentField.getText().trim();
                String designation = designationField.getText().trim();

                // Validation
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
                    fullName.isEmpty() || email.isEmpty() || department.isEmpty() || designation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters long.", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isValidEmail(email)) {
                    JOptionPane.showMessageDialog(dialog, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                addUser(username, password, role, fullName, email, department, designation);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding user: " + ex.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(saveButton, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addUser(String username, String password, String role, String fullName, String email,
                        String department, String designation) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // Generate user ID (8 characters: USR + 5 digits)
            String userId = "USR" + String.format("%05d", (int)(Math.random() * 100000));

            // Check if user ID already exists
            String checkSql = "SELECT COUNT(*) FROM user_accounts WHERE user_id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // If ID exists, generate a new one
                        userId = "USR" + String.format("%05d", (int)(Math.random() * 100000));
                    }
                }
            }

            // Check if username already exists
            String usernameCheckSql = "SELECT COUNT(*) FROM user_accounts WHERE username = ?";
            try (PreparedStatement usernameCheckStmt = connection.prepareStatement(usernameCheckSql)) {
                usernameCheckStmt.setString(1, username);
                try (ResultSet rs = usernameCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Username already exists. Please choose another.");
                    }
                }
            }

            // Check if email already exists
            String emailCheckSql = "SELECT COUNT(*) FROM employees WHERE email = ?";
            try (PreparedStatement emailCheckStmt = connection.prepareStatement(emailCheckSql)) {
                emailCheckStmt.setString(1, email);
                try (ResultSet rs = emailCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Email already exists. Please use another email.");
                    }
                }
            }

            // Hash password
            String hashedPassword = hashPassword(password);

            // Insert into user_accounts
            String userSql = "INSERT INTO user_accounts (user_id, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                userStmt.setString(1, userId);
                userStmt.setString(2, username);
                userStmt.setString(3, hashedPassword);
                userStmt.setString(4, role);
                userStmt.executeUpdate();
            }

            // Insert into employees
            String empSql = "INSERT INTO employees (employee_id, full_name, email, department, designation) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement empStmt = connection.prepareStatement(empSql)) {
                empStmt.setString(1, userId);
                empStmt.setString(2, fullName);
                empStmt.setString(3, email);
                empStmt.setString(4, department);
                empStmt.setString(5, designation);
                empStmt.executeUpdate();
            }

            connection.commit();
            JOptionPane.showMessageDialog(this, "User added successfully with ID: " + userId);
            loadUsers();
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
        } catch (SQLException e) {
                    Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    DBConnection.closeConnection();
                } catch (SQLException e) {
                    Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }
        
        String userId = (String) userTable.getValueAt(selectedRow, 0);
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        try {
            Connection connection = DBConnection.getConnection();
            String sql = "SELECT u.*, e.full_name, e.email, e.department, e.designation " +
                        "FROM user_accounts u " +
                        "LEFT JOIN employees e ON u.user_id = e.employee_id " +
                        "WHERE u.user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Username
                        gbc.gridx = 0;
                        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
                        JTextField usernameField = new JTextField(rs.getString("username"), 20);
        panel.add(usernameField, gbc);
        
                        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
                        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
                        JPasswordField passwordField = new JPasswordField(20);
                        panel.add(passwordField, gbc);
        
                        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
                        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
                        JPasswordField confirmPasswordField = new JPasswordField(20);
                        panel.add(confirmPasswordField, gbc);
        
                        // Role
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
                        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"employee", "developer", "admin"});
                        roleCombo.setSelectedItem(rs.getString("role"));
                        panel.add(roleCombo, gbc);
                        
                        // Full Name
                        gbc.gridx = 0;
                        gbc.gridy = 4;
                        panel.add(new JLabel("Full Name:"), gbc);
                        gbc.gridx = 1;
                        JTextField fullNameField = new JTextField(rs.getString("full_name"), 20);
                        panel.add(fullNameField, gbc);

                        // Email
                        gbc.gridx = 0;
                        gbc.gridy = 5;
                        panel.add(new JLabel("Email:"), gbc);
                        gbc.gridx = 1;
                        JTextField emailField = new JTextField(rs.getString("email"), 20);
                        panel.add(emailField, gbc);

                        // Department
                        gbc.gridx = 0;
                        gbc.gridy = 6;
                        panel.add(new JLabel("Department:"), gbc);
                        gbc.gridx = 1;
                        JTextField departmentField = new JTextField(rs.getString("department"), 20);
                        panel.add(departmentField, gbc);

                        // Designation
        gbc.gridx = 0;
                        gbc.gridy = 7;
                        panel.add(new JLabel("Designation:"), gbc);
                        gbc.gridx = 1;
                        JTextField designationField = new JTextField(rs.getString("designation"), 20);
                        panel.add(designationField, gbc);

                        // Save button
                        gbc.gridx = 0;
                        gbc.gridy = 8;
        gbc.gridwidth = 2;
                        JButton saveButton = createStyledButton("Save");
                        saveButton.addActionListener(e -> {
                            try {
                                String username = usernameField.getText().trim();
                                String password = new String(passwordField.getPassword()).trim();
                                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                                String role = (String) roleCombo.getSelectedItem();
                                String fullName = fullNameField.getText().trim();
                                String email = emailField.getText().trim();
                                String department = departmentField.getText().trim();
                                String designation = designationField.getText().trim();

                                // Validation
                                if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || 
                                    department.isEmpty() || designation.isEmpty()) {
                                    JOptionPane.showMessageDialog(dialog, "All fields are required.", 
                                                                "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                // If password is provided, validate it
                                if (!password.isEmpty()) {
                                    if (!password.equals(confirmPassword)) {
                                        JOptionPane.showMessageDialog(dialog, "Passwords do not match.", 
                                                                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
                                    if (password.length() < 6) {
                                        JOptionPane.showMessageDialog(dialog, 
                                            "Password must be at least 6 characters long.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                }

                                if (!isValidEmail(email)) {
                                    JOptionPane.showMessageDialog(dialog, "Invalid email format.", 
                                                                "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                updateUser(userId, username, password, role, fullName, email, 
                                         department, designation);
                                dialog.dispose();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, "Error updating user: " + ex.getMessage(), 
                                                            "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        panel.add(saveButton, gbc);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading user details: " + ex.getMessage());
        }
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateUser(String userId, String username, String password, String role, String fullName,
                          String email, String department, String designation) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // Check if username already exists (excluding current user)
            String usernameCheckSql = "SELECT COUNT(*) FROM user_accounts WHERE username = ? AND user_id != ?";
            try (PreparedStatement usernameCheckStmt = connection.prepareStatement(usernameCheckSql)) {
                usernameCheckStmt.setString(1, username);
                usernameCheckStmt.setString(2, userId);
                try (ResultSet rs = usernameCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Username already exists. Please choose another.");
                    }
                }
            }

            // Check if email already exists (excluding current user)
            String emailCheckSql = "SELECT COUNT(*) FROM employees WHERE email = ? AND employee_id != ?";
            try (PreparedStatement emailCheckStmt = connection.prepareStatement(emailCheckSql)) {
                emailCheckStmt.setString(1, email);
                emailCheckStmt.setString(2, userId);
                try (ResultSet rs = emailCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("Email already exists. Please use another email.");
                    }
                }
            }

            // Update user_accounts
            String userSql;
            if (!password.isEmpty()) {
                // If password is provided, update it
                String hashedPassword = hashPassword(password);
                userSql = "UPDATE user_accounts SET username = ?, password = ?, role = ? WHERE user_id = ?";
                try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                    userStmt.setString(1, username);
                    userStmt.setString(2, hashedPassword);
                    userStmt.setString(3, role);
                    userStmt.setString(4, userId);
                    userStmt.executeUpdate();
                }
            } else {
                // If password is not provided, don't update it
                userSql = "UPDATE user_accounts SET username = ?, role = ? WHERE user_id = ?";
                try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                    userStmt.setString(1, username);
                    userStmt.setString(2, role);
                    userStmt.setString(3, userId);
                    userStmt.executeUpdate();
                }
            }

            // Update employees
            String empSql = "UPDATE employees SET full_name = ?, email = ?, department = ?, designation = ? WHERE employee_id = ?";
            try (PreparedStatement empStmt = connection.prepareStatement(empSql)) {
                empStmt.setString(1, fullName);
                empStmt.setString(2, email);
                empStmt.setString(3, department);
                empStmt.setString(4, designation);
                empStmt.setString(5, userId);
                empStmt.executeUpdate();
            }

            connection.commit();
            JOptionPane.showMessageDialog(this, "User updated successfully");
            loadUsers();
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
        } catch (SQLException e) {
                    Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    DBConnection.closeConnection();
                } catch (SQLException e) {
                    Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        String userId = (String) userTable.getValueAt(selectedRow, 0);
        String username = (String) userTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user: " + username + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            Connection connection = null;
            try {
                connection = DBConnection.getConnection();
                connection.setAutoCommit(false);

                // Delete from employees first (due to foreign key constraint)
                String empSql = "DELETE FROM employees WHERE employee_id = ?";
                try (PreparedStatement empStmt = connection.prepareStatement(empSql)) {
                    empStmt.setString(1, userId);
                    empStmt.executeUpdate();
                }

                // Delete from user_accounts
                String userSql = "DELETE FROM user_accounts WHERE user_id = ?";
                try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
                    userStmt.setString(1, userId);
                    userStmt.executeUpdate();
                }

                connection.commit();
                JOptionPane.showMessageDialog(this, "User deleted successfully");
                loadUsers();
            } catch (SQLException ex) {
                if (connection != null) {
                    try {
                        connection.rollback();
            } catch (SQLException e) {
                        Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        DBConnection.closeConnection();
                    } catch (SQLException e) {
                        Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
        }
    }

    private void showUserDetails() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view details");
            return;
        }

        String userId = (String) userTable.getValueAt(selectedRow, 0);
        JDialog dialog = new JDialog(this, "User Details", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        try {
            Connection connection = DBConnection.getConnection();
            String sql = "SELECT u.*, e.full_name, e.department, e.designation " +
                        "FROM user_accounts u " +
                        "JOIN employees e ON u.user_id = e.employee_id " +
                        "WHERE u.user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int row = 0;
                        addDetailRow(panel, gbc, "User ID:", rs.getString("user_id"), row++);
                        addDetailRow(panel, gbc, "Username:", rs.getString("username"), row++);
                        addDetailRow(panel, gbc, "Role:", rs.getString("role"), row++);
                        addDetailRow(panel, gbc, "Full Name:", rs.getString("full_name"), row++);
                        addDetailRow(panel, gbc, "Department:", rs.getString("department"), row++);
                        addDetailRow(panel, gbc, "Designation:", rs.getString("designation"), row++);
                        addDetailRow(panel, gbc, "Created At:", rs.getTimestamp("created_at").toString(), row++);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeveloperDashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading user details: " + ex.getMessage());
        }

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(value), gbc);
    }
}


