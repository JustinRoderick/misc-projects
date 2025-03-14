/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: DatabaseConnection
*/

package com.project3.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection(Properties dbProperties, String username, String password) throws SQLException {
        String url = dbProperties.getProperty("db.url");
        
        Properties connectionProps = new Properties();
        connectionProps.setProperty("user", username);
        connectionProps.setProperty("password", password);
        
        return DriverManager.getConnection(url, connectionProps);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log error or handle appropriately
                e.printStackTrace();
            }
        }
    }
} 