package com.project4.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties = new Properties();

    public static Connection getConnection(String userType) throws SQLException {
        loadProperties(userType);
        try {
            Class.forName(properties.getProperty("db.driver"));
            return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        }
    }

    private static void loadProperties(String userType) throws SQLException {
        String propertiesFile = "/properties/" + userType + ".properties";
        try (InputStream input = DatabaseUtil.class.getResourceAsStream(propertiesFile)) {
            if (input == null) {
                throw new SQLException("Properties file not found: " + propertiesFile);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException("Error loading properties file", e);
        }
    }
}
