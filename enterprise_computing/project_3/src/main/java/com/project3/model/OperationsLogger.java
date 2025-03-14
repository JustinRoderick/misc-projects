/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: OperationsLogger
*/

package com.project3.model;

import com.project3.util.DatabaseConnection;
import com.project3.util.PropertiesLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class OperationsLogger {
    private static final String PROJECT3APP_PROPERTIES = "project3app.properties";
    private static final String UPDATE_QUERY = 
        "INSERT INTO operationscount (login_username, num_queries, num_updates) " +
        "VALUES (?, 1, 0) " +
        "ON DUPLICATE KEY UPDATE num_queries = num_queries + ?";
    private static final String UPDATE_UPDATE = 
        "INSERT INTO operationscount (login_username, num_queries, num_updates) " +
        "VALUES (?, 0, 1) " +
        "ON DUPLICATE KEY UPDATE num_updates = num_updates + ?";

    public static void logOperation(String username, boolean isQuery) {
        try {
            Properties props = PropertiesLoader.loadProperties(PROJECT3APP_PROPERTIES);
            try (Connection conn = DatabaseConnection.getConnection(
                    props, 
                    props.getProperty("user.username"), 
                    props.getProperty("user.password"))) {
                
                String sql = isQuery ? UPDATE_QUERY : UPDATE_UPDATE;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setInt(2, 1);
                    stmt.executeUpdate();
                }
            }
        } catch (IOException | SQLException e) {
            // Log error but don't interrupt the main application flow
            e.printStackTrace();
        }
    }
} 