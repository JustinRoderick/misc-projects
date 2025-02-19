/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: YardConfig
Description: Loads and stores the possible yard configuration
*/

package org.railroad;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class YardConfig {
    private Map<TrackPair, List<Integer>> pathMap = new HashMap<>();

    public void load(String filename) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 5) continue;
                int inbound = Integer.parseInt(parts[0].trim());
                int switchOne = Integer.parseInt(parts[1].trim());
                int switchTwo = Integer.parseInt(parts[2].trim());
                int switchThree = Integer.parseInt(parts[3].trim());
                int outbound = Integer.parseInt(parts[4].trim());
                TrackPair key = new TrackPair(inbound, outbound);
                List<Integer> switches = Arrays.asList(switchOne, switchTwo, switchThree);
                pathMap.put(key, switches);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getSwitches(int inbound, int outbound) {
        return pathMap.get(new TrackPair(inbound, outbound));
    }
}