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
    private static final String DB_PROPERTIES = "operationslog.properties";
    private static final String USER_PROPERTIES = "theaccountant.properties";
    private JComboBox<String> dbUrlPropertiesCombo;
    private JComboBox<String> userPropertiesCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea sqlCommandArea;
    private JTable resultTable;
    private JTextArea connectionStatusArea;
    private Connection connection;

    public AccountantGUI() {
        setTitle("Accountant Application");
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

        dbUrlPropertiesCombo = new JComboBox<>(new String[]{DB_PROPERTIES});
        userPropertiesCombo = new JComboBox<>(new String[]{USER_PROPERTIES});
        dbUrlPropertiesCombo.setEnabled(false);
        userPropertiesCombo.setEnabled(false);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton connectButton = new JButton("Connect to Database");
        JButton disconnectButton = new JButton("Disconnect from Database");

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("DB URL Properties File:"), gbc);
        gbc.gridx = 1;
        panel.add(dbUrlPropertiesCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("User Properties File:"), gbc);
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

            if (!PropertiesLoader.validateUserCredentials(USER_PROPERTIES, username, password)) {
                JOptionPane.showMessageDialog(this,
                    "Invalid credentials for accountant access",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Properties props = PropertiesLoader.loadProperties(DB_PROPERTIES);
            connection = DatabaseConnection.getConnection(props, username, password);
            
            updateConnectionStatus("Connected to " + props.getProperty("db.url"));
            
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error connecting to database: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
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
                JOptionPane.showMessageDialog(this,
                    "Only SELECT queries are allowed for the accountant user",
                    "Permission Error",
                    JOptionPane.ERROR_MESSAGE);
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
            AccountantGUI gui = new AccountantGUI();
            gui.setVisible(true);
        });
    }
} 