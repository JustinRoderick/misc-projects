/* Name: Justin Roderick
Course: CNT 4714 – Spring 2025 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: April 23, 2025
*/

package com.project4.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private static Properties loadProperties(String role) throws IOException {
        Properties props = new Properties();
        String propFile = role + ".properties";
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("properties/" + propFile)) {
            if (input == null) {
                throw new IOException("Unable to find " + propFile);
            }
            props.load(input);
        }
        return props;
    }

    private static Connection getConnection(String role) throws SQLException {
        try {
            Properties props = loadProperties(role);
            Class.forName(props.getProperty("db.driver"));
            return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
        } catch (ClassNotFoundException | IOException e) {
            throw new SQLException("Failed to establish database connection: " + e.getMessage());
        }
    }

    public static Connection getAccountantConnection() throws SQLException {
        return getConnection("accountant");
    }

    public static Connection getClientConnection() throws SQLException {
        return getConnection("client");
    }

    public static Connection getDataEntryConnection() throws SQLException {
        return getConnection("dataentry");
    }

    public static Connection getSystemConnection() throws SQLException {
        return getConnection("system");
    }

    public static Connection getAuthConnection() throws SQLException {
        return getConnection("auth");
    }
}
