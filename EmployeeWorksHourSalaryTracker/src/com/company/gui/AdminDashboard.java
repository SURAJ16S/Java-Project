package com.company.gui;

import com.company.database.DBConnection;
import com.company.utils.QRCodeGenerator;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.image.BufferedImage;

public class AdminDashboard extends JFrame {
  
    // A status label at the bottom serves as our status bar.
    private JLabel statusBar;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Main panel to hold everything with a BorderLayout
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

        // Center panel using BoxLayout vertically for our buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(240, 240, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create buttons with a stylish look
        JButton jobAllocationBtn = createStyledButton("Job Allocation");
        jobAllocationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Assuming JobAllocationPanel is another JFrame or dialog.
                new JobAllocationPanel().setVisible(true);
                dispose();
            }
        });

        JButton viewEmployeesBtn = createStyledButton("Display/Search Employees");
        viewEmployeesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayEmployees();
            }
        });

        JButton paySalaryBtn = createStyledButton("Pay Salary");
        paySalaryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paySalary();
            }
        });

        JButton logoutBtn = createStyledButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Assuming LoginFrame is the login screen of your application.
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // Add buttons to the panel with spacing between each
        buttonPanel.add(jobAllocationBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(viewEmployeesBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(paySalaryBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(logoutBtn);

        // Status bar for notifications
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setForeground(new Color(100, 100, 100));
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(230, 230, 230));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusPanel.add(statusBar, BorderLayout.WEST);

        // Add components to the main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // A helper method to create a styled JButton
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setMaximumSize(new Dimension(300, 50));
        button.setPreferredSize(new Dimension(300, 50));
        
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

    // Display the list of employees using a dialog box.
    private void displayEmployees() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM employees";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getString("employee_id"))
                  .append(", Name: ").append(rs.getString("full_name"))
                  .append(", Dept: ").append(rs.getString("department"))
                  .append(", Designation: ").append(rs.getString("designation"))
                  .append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Employee Details", JOptionPane.INFORMATION_MESSAGE);
            statusBar.setText("Employee details displayed successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying employees: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setText("Error displaying employees.");
        }
    }

    // Process salary payment and generate a QR code for confirmation.
    private void paySalary() {
        String empId = JOptionPane.showInputDialog(this, "Enter Employee ID for salary payment:");
        if (empId == null || empId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            statusBar.setText("Payment aborted: No Employee ID provided.");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();

            // For demonstration, counting attendance days for salary calculation.
            String query = "SELECT COUNT(*) AS daysWorked FROM attendance WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, empId);
            ResultSet rs = pst.executeQuery();
            int daysWorked = 0;
            if (rs.next()) {
                daysWorked = rs.getInt("daysWorked");
            }
            // Assume a fixed daily rate of ₹1000.
            double totalSalary = daysWorked * 1000;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Total salary for " + empId + " is ₹" + totalSalary + ". Proceed with payment?",
                    "Confirm Payment", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String paymentQuery = "INSERT INTO salary_payments(employee_id, payment_date, amount) VALUES (?, CURDATE(), ?)";
                PreparedStatement pst2 = con.prepareStatement(paymentQuery);
                pst2.setString(1, empId);
                pst2.setDouble(2, totalSalary);
                pst2.executeUpdate();

                // Generate a QR code for the payment confirmation (implementation from QRCodeGenerator is assumed).
                BufferedImage qr = QRCodeGenerator.generateQRCode("Employee: " + empId + " Salary: ₹" + totalSalary);
                JOptionPane.showMessageDialog(this, new ImageIcon(qr), "Salary Payment QR Code", JOptionPane.PLAIN_MESSAGE);
                statusBar.setText("Salary payment successful. QR code generated.");
            } else {
                statusBar.setText("Salary payment cancelled.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during salary payment: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            statusBar.setText("Error during salary payment.");
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
