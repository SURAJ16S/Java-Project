package com.company.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DeveloperDashboard extends JFrame {
    public DeveloperDashboard(){
        setTitle("Developer Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }
    
    private void initComponents(){
        JPanel panel = new JPanel(new GridLayout(3,1,10,10));
        JButton createUserBtn = new JButton("Create User");
        createUserBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Create User functionality not implemented.");
            }
        });
        JButton deleteUserBtn = new JButton("Delete User");
        deleteUserBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Delete User functionality not implemented.");
            }
        });
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        panel.add(createUserBtn);
        panel.add(deleteUserBtn);
        panel.add(backBtn);
        add(panel);
    }
}
