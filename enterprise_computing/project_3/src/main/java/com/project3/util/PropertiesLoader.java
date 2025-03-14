/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 14, 2025
Class: PropertiesLoader
*/

package com.project3.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final String PROPERTIES_DIR = "properties/";

    public static Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_DIR + filename)) {
            if (input == null) {
                throw new IOException("Properties file not found: " + filename);
            }
            properties.load(input);
        }
        return properties;
    }

    public static boolean validateUserCredentials(String propertiesFile, String username, String password) throws IOException {
        Properties userProps = loadProperties(propertiesFile);
        return username.equals(userProps.getProperty("user.username")) &&
               password.equals(userProps.getProperty("user.password"));
    }
} 