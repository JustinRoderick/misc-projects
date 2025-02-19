/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: Main
Description: Main class that loads the files and runs the simulation
*/

package org.railroad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS........... $ $ $");

        YardConfig yardConfig = new YardConfig();
        yardConfig.load("yardFile.csv");

        SwitchManager switchManager = new SwitchManager(10);

        List<Train> trains = FileParser.parse("fleetFile.csv", yardConfig, switchManager);

        ExecutorService executor = Executors.newFixedThreadPool(30);

        for (Train train : trains) {
            executor.submit(train);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("$ $ $ SIMULATION ENDS $ $ $");
        printSummary(trains);

    }

    private static void printSummary(List<Train> trains) {
        System.out.println("\nSummary:");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        trains.forEach(train -> {
            System.out.printf("Train Number %d:\n", train.getNumber());
            System.out.printf("%-15s %-15s %-15s %-12s %-12s %-12s %-10s %-15s %-18s%n",
                    "Train Number", "Inbound Track", "Outbound Track", "Switch 1", "Switch 2", "Switch 3",  "Hold", "Dispatched",
                    "Dispatch Sequence");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
            List switches = train.getSwitches() != null ? train.getSwitches() : Arrays.asList(0, 0, 0);
            System.out.printf("%-15d %-15d %-15d %-12s %-12s %-12s %-10s %-15s %-18s%n",
                    train.getNumber(),
                    train.getInbound(),
                    train.getOutbound(),
                    switches.get(0),
                    switches.get(1),
                    switches.get(2),
                    train.isOnHold() ? "True" : "False",
                    train.isDispatched() ? "True" : "False",
                    train.getDispatchSequence()
            );
            System.out.println();
        });
    }
}