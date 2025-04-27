package com.company.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatIntelliJLaf;

public class DeveloperDashboard extends JFrame {
    public DeveloperDashboard(){
        setTitle("Developer Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new GridLayout(3,1,10,10));
        JButton createUserBtn = new JButton("Create User");
        createUserBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(null, "Create User functionality not implemented.");
            }
        });
        JButton deleteUserBtn = new JButton("Delete User");
        deleteUserBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(null, "Delete User functionality not implemented.");
            }
        });
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        panel.add(createUserBtn);
        panel.add(deleteUserBtn);
        panel.add(backBtn);
        add(panel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DeveloperDashboard().setVisible(true);
            }
        });
    }
}
