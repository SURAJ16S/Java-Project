package com.company.gui;

import com.company.database.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DeveloperDashboard extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(DeveloperDashboard.class.getName());
    private JLabel statusLabel;
    private JPanel mainPanel;
    private JTable userTable;
    private JTextField searchField;
    
    public DeveloperDashboard() {
        setTitle("Developer Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadUsers();
    }
    
    private void initComponents() {
        // Main panel with a nice background
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(240, 240, 245));
        JLabel titleLabel = new JLabel("Developer Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 245));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton searchBtn = createStyledButton("Search");
        searchBtn.addActionListener(e -> searchUsers());
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table model
        String[] columns = {"ID", "Username", "Full Name", "Email", "Role", "Status"};
        userTable = new JTable(new Object[0][6], columns);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.setRowHeight(25);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton createUserBtn = createStyledButton("Create User");
        createUserBtn.addActionListener(e -> showCreateUserDialog());
        
        JButton editUserBtn = createStyledButton("Edit User");
        editUserBtn.addActionListener(e -> editSelectedUser());
        
        JButton deleteUserBtn = createStyledButton("Delete User");
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        
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
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        buttonPanel.add(createUserBtn);
        buttonPanel.add(editUserBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(backBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Status bar
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
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
    
    private void loadUsers() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT id, username, full_name, email, role, status FROM user_accounts";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            // Count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            
            // Create data array
            Object[][] data = new Object[rowCount][6];
            int row = 0;
            while (rs.next()) {
                data[row][0] = rs.getInt("id");
                data[row][1] = rs.getString("username");
                data[row][2] = rs.getString("full_name");
                data[row][3] = rs.getString("email");
                data[row][4] = rs.getString("role");
                data[row][5] = rs.getString("status");
                row++;
            }
            
            // Update table model
            userTable.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String[] {"ID", "Username", "Full Name", "Email", "Role", "Status"}
            ));
            
            statusLabel.setText("Users loaded successfully");
            LOGGER.info("Users loaded successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading users", e);
            statusLabel.setText("Error loading users: " + e.getMessage());
        }
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUsers();
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT id, username, full_name, email, role, status FROM user_accounts " +
                          "WHERE username LIKE ? OR full_name LIKE ? OR email LIKE ?";
            PreparedStatement pst = conn.prepareStatement(query);
            String term = "%" + searchTerm + "%";
            pst.setString(1, term);
            pst.setString(2, term);
            pst.setString(3, term);
            
            ResultSet rs = pst.executeQuery();
            
            // Count rows
            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            
            // Create data array
            Object[][] data = new Object[rowCount][6];
            int row = 0;
            while (rs.next()) {
                data[row][0] = rs.getInt("id");
                data[row][1] = rs.getString("username");
                data[row][2] = rs.getString("full_name");
                data[row][3] = rs.getString("email");
                data[row][4] = rs.getString("role");
                data[row][5] = rs.getString("status");
                row++;
            }
            
            // Update table model
            userTable.setModel(new javax.swing.table.DefaultTableModel(
                data,
                new String[] {"ID", "Username", "Full Name", "Email", "Role", "Status"}
            ));
            
            statusLabel.setText("Found " + rowCount + " users matching '" + searchTerm + "'");
            LOGGER.info("Search completed for term: " + searchTerm);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching users", e);
            statusLabel.setText("Error searching users: " + e.getMessage());
        }
    }
    
    private void showCreateUserDialog() {
        JDialog dialog = new JDialog(this, "Create User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "employee", "developer"});
        
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e -> {
            createUser(usernameField.getText(),
                      new String(passwordField.getPassword()),
                      fullNameField.getText(),
                      emailField.getText(),
                      (String)roleBox.getSelectedItem());
            dialog.dispose();
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void createUser(String username, String password, String fullName, String email, String role) {
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "INSERT INTO user_accounts (username, password, full_name, email, role, status) " +
                          "VALUES (?, ?, ?, ?, ?, 'active')";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password); // In a real application, this should be hashed
            pst.setString(3, fullName);
            pst.setString(4, email);
            pst.setString(5, role);
            
            pst.executeUpdate();
            
            statusLabel.setText("User created successfully");
            LOGGER.info("User created: " + username);
            loadUsers(); // Refresh the table
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            statusLabel.setText("Error creating user: " + e.getMessage());
        }
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }
        
        // Get user data from selected row
        int userId = (int)userTable.getValueAt(selectedRow, 0);
        String username = (String)userTable.getValueAt(selectedRow, 1);
        String fullName = (String)userTable.getValueAt(selectedRow, 2);
        String email = (String)userTable.getValueAt(selectedRow, 3);
        String role = (String)userTable.getValueAt(selectedRow, 4);
        
        // Show edit dialog
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        JTextField usernameField = new JTextField(username, 20);
        JTextField fullNameField = new JTextField(fullName, 20);
        JTextField emailField = new JTextField(email, 20);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"admin", "employee", "developer"});
        roleBox.setSelectedItem(role);
        
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> {
            updateUser(userId,
                      usernameField.getText(),
                      fullNameField.getText(),
                      emailField.getText(),
                      (String)roleBox.getSelectedItem());
            dialog.dispose();
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void updateUser(int userId, String username, String fullName, String email, String role) {
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required");
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            String query = "UPDATE user_accounts SET username = ?, full_name = ?, email = ?, role = ? " +
                          "WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, fullName);
            pst.setString(3, email);
            pst.setString(4, role);
            pst.setInt(5, userId);
            
            pst.executeUpdate();
            
            statusLabel.setText("User updated successfully");
            LOGGER.info("User updated: " + username);
            loadUsers(); // Refresh the table
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            statusLabel.setText("Error updating user: " + e.getMessage());
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        int userId = (int)userTable.getValueAt(selectedRow, 0);
        String username = (String)userTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user '" + username + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM user_accounts WHERE id = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, userId);
                
                pst.executeUpdate();
                
                statusLabel.setText("User deleted successfully");
                LOGGER.info("User deleted: " + username);
                loadUsers(); // Refresh the table
                
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error deleting user", e);
                statusLabel.setText("Error deleting user: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> new DeveloperDashboard().setVisible(true));
    }
}
