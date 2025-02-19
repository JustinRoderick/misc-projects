/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: FileParser
Description: Reads the fleet file and creates the train objects with the needed switches
*/

package org.railroad;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    public static List<Train> parse(String filename, YardConfig yardConfig, SwitchManager switchManager) {
        List<Train> trains = new ArrayList<>();
        try (InputStream inputStream = FileParser.class.getClassLoader().getResourceAsStream(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;
                int number = Integer.parseInt(parts[0].trim());
                int inbound = Integer.parseInt(parts[1].trim());
                int outbound = Integer.parseInt(parts[2].trim());
                List<Integer> requiredSwitches = yardConfig.getSwitches(inbound, outbound);
                trains.add(new Train(number, inbound, outbound, requiredSwitches, switchManager));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trains;
    }
}