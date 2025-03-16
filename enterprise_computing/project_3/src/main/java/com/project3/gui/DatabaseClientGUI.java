/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: DatabaseClientGUI
*/

package com.project3.gui;

import com.project3.model.OperationsLogger;
import com.project3.util.DatabaseConnection;
import com.project3.util.PropertiesLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

public class DatabaseClientGUI extends JFrame {
    private JComboBox<String> dbUrlPropertiesCombo;
    private JComboBox<String> userPropertiesCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea sqlCommandArea;
    private JTextArea connectionStatusArea;
    private JTable resultTable;
    private JScrollPane resultScrollPane;
    private Connection connection;
    private String currentUsername;

    public DatabaseClientGUI() {
        setTitle("SQL Client Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JPanel connectionPanel = createConnectionPanel();
        JPanel commandPanel = createCommandPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();

        topPanel.add(connectionPanel);
        topPanel.add(commandPanel);
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        updateConnectionStatus("Not Connected");
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Database Connection"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        dbUrlPropertiesCombo = new JComboBox<>(new String[]{"project3.properties", "bikedb.properties"});
        userPropertiesCombo = new JComboBox<>(new String[]{"root.properties", "client1.properties", "client2.properties"});
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton connectButton = new JButton("Connect to Database");
        JButton disconnectButton = new JButton("Disconnect from Database");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Properties File:"), gbc);
        gbc.gridx = 1;
        panel.add(dbUrlPropertiesCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username File:"), gbc);
        gbc.gridx = 1;
        panel.add(userPropertiesCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(connectButton, gbc);

        gbc.gridy = 5;
        panel.add(disconnectButton, gbc);

        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());

        return panel;
    }

    private JPanel createCommandPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enter an SQL Command"));

        sqlCommandArea = new JTextArea(8, 40);
        sqlCommandArea.setLineWrap(true);
        sqlCommandArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(sqlCommandArea);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton executeButton = new JButton("Execute SQL Command");
        JButton clearButton = new JButton("Clear SQL Command");
        buttonPanel.add(executeButton);
        buttonPanel.add(clearButton);

        executeButton.addActionListener(e -> executeSqlCommand());
        clearButton.addActionListener(e -> sqlCommandArea.setText(""));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        connectionStatusArea = new JTextArea(2, 50);
        connectionStatusArea.setEditable(false);
        connectionStatusArea.setBackground(new Color(240, 240, 240));
        panel.add(new JScrollPane(connectionStatusArea), BorderLayout.NORTH);

        resultTable = new JTable();
        resultScrollPane = new JScrollPane(resultTable);
        panel.add(resultScrollPane, BorderLayout.CENTER);

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
            String dbPropertiesFile = (String) dbUrlPropertiesCombo.getSelectedItem();
            String userPropertiesFile = (String) userPropertiesCombo.getSelectedItem();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            System.out.println("Attempting to connect with:");
            System.out.println("DB Properties: " + dbPropertiesFile);
            System.out.println("User Properties: " + userPropertiesFile);
            System.out.println("Username: " + username);
            System.out.println("Password length: " + password.length());

            Properties dbProps = PropertiesLoader.loadProperties(dbPropertiesFile);
            System.out.println("Database URL: " + dbProps.getProperty("db.url"));

            if (!PropertiesLoader.validateUserCredentials(userPropertiesFile, username, password)) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid credentials: Username/password do not match properties file",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            connection = DatabaseConnection.getConnection(dbProps, username, password);
            currentUsername = username;
            
            updateConnectionStatus("Connected to " + dbProps.getProperty("db.url"));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection error: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading properties: " + e.getMessage(),
                "Properties Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                currentUsername = null;
                updateConnectionStatus("Not Connected");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error disconnecting: " + e.getMessage());
        }
    }

    private void executeSqlCommand() {
        if (connection == null || sqlCommandArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please connect to database and enter an SQL command");
            return;
        }

        String sql = sqlCommandArea.getText().trim();
        try {
            if (sql.toLowerCase().startsWith("select")) {
                executeQuery(sql);
            } else {
                executeUpdate(sql);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error executing SQL: " + e.getMessage(),
                "SQL Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeQuery(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            resultTable.setModel(new DefaultTableModel(data, columnNames));
            
            OperationsLogger.logOperation(currentUsername, true);
            
        }
    }

    private void executeUpdate(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,
                rowsAffected + " row(s) affected",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
            OperationsLogger.logOperation(currentUsername, false);
        }
    }

    private void updateConnectionStatus(String status) {
        connectionStatusArea.setText(status);
    }

    private void clearResults() {
        resultTable.setModel(new DefaultTableModel());
    }

    private void closeApplication() {
        disconnectFromDatabase();
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
            DatabaseClientGUI gui = new DatabaseClientGUI();
            gui.setVisible(true);
        });
    }
} 