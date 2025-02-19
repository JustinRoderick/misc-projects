/*
Name: Justin Roderick
Course: CNT 4714 Spring 2025
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 18, 2025
Class: Main
Description: a description of what the class provides would normally be expected.
*/

package org.railroad;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    }
}