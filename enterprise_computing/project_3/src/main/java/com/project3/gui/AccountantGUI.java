/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Specialized Accountant Application
Date: March 14, 2025
Class: AccountantGUI
*/

package com.project3.gui;

import com.project3.util.DatabaseConnection;
import com.project3.util.PropertiesLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

public class AccountantGUI extends JFrame {
    private static final String ACCOUNTANT_PROPERTIES = "theaccountant.properties";
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTable resultTable;
    private JTextArea connectionStatusArea;
    private Connection connection;

    public AccountantGUI() {
        setTitle("Operations Monitoring Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);

        add(createConnectionPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        updateConnectionStatus("Not Connected");
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Database Connection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton connectButton = new JButton("Connect to Database");
        JButton viewOperationsButton = new JButton("View All Operations");

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(connectButton, gbc);

        gbc.gridy = 3;
        panel.add(viewOperationsButton, gbc);

        // Add action listeners
        connectButton.addActionListener(e -> connectToDatabase());
        viewOperationsButton.addActionListener(e -> viewAllOperations());

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Connection status area
        connectionStatusArea = new JTextArea(2, 50);
        connectionStatusArea.setEditable(false);
        connectionStatusArea.setBackground(new Color(240, 240, 240));
        panel.add(new JScrollPane(connectionStatusArea), BorderLayout.NORTH);

        // Results table
        resultTable = new JTable();
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton clearResultsButton = new JButton("Clear Result Window");
        JButton exitButton = new JButton("Close Application");

        clearResultsButton.addActionListener(e -> clearResults());
        exitButton.addActionListener(e -> closeApplication());

        panel.add(clearResultsButton);
        panel.add(exitButton);

        return panel;
    }

    private void connectToDatabase() {
        try {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Validate credentials
            if (!PropertiesLoader.validateUserCredentials(ACCOUNTANT_PROPERTIES, username, password)) {
                JOptionPane.showMessageDialog(this,
                    "Invalid credentials for accountant access",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Load properties and connect
            Properties props = PropertiesLoader.loadProperties(ACCOUNTANT_PROPERTIES);
            connection = DatabaseConnection.getConnection(props, username, password);
            
            updateConnectionStatus("Connected to " + props.getProperty("db.url"));
            
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error connecting to database: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewAllOperations() {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Please connect to database first");
            return;
        }

        try {
            String sql = "SELECT * FROM operationscount";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Get column names
                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }

                // Get data
                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }

                resultTable.setModel(new DefaultTableModel(data, columnNames));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving operations data: " + e.getMessage(),
                "Query Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateConnectionStatus(String status) {
        connectionStatusArea.setText(status);
    }

    private void clearResults() {
        resultTable.setModel(new DefaultTableModel());
    }

    private void closeApplication() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            AccountantGUI gui = new AccountantGUI();
            gui.setVisible(true);
        });
    }
} 